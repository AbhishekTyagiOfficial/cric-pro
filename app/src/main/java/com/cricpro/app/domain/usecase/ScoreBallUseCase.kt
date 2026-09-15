package com.cricpro.app.domain.usecase

import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import com.cricpro.app.domain.repository.MatchRepository
import com.cricpro.app.domain.repository.ScoringRepository
import com.cricpro.app.domain.repository.SyncRepository
import javax.inject.Inject

class ScoreBallUseCase @Inject constructor(
    private val scoringRepository: ScoringRepository,
    private val matchRepository: MatchRepository,
    private val syncRepository: SyncRepository,
    private val scoringEngine: ScoringEngine
) {
    suspend operator fun invoke(currentMatch: Match, ball: Ball): Result<ScoringEngine.ScoringResult> {
        if (currentMatch.status == MatchStatus.COMPLETED) {
            return Result.failure(IllegalStateException("Match is already completed!"))
        }

        var inningsNum = currentMatch.currentInningsNumber
        var inn1 = currentMatch.firstInnings
        var inn2 = currentMatch.secondInnings

        // Auto-advance to innings 2 if innings 1 is completed
        if (inningsNum == 1 && inn1?.isCompleted == true) {
            inningsNum = 2
        }

        val currentInnings = (if (inningsNum == 1) inn1 else inn2) ?: Innings(
            inningsNumber = inningsNum,
            battingTeamId = if (inningsNum == 1) currentMatch.teamA.teamId else currentMatch.teamB.teamId,
            bowlingTeamId = if (inningsNum == 1) currentMatch.teamB.teamId else currentMatch.teamA.teamId,
            target = if (inningsNum == 2) (inn1?.totalRuns ?: 0) + 1 else null
        )

        if (currentInnings.isCompleted) {
            if (inningsNum == 1) {
                inningsNum = 2
            } else {
                return Result.failure(IllegalStateException("Match is already completed!"))
            }
        }

        val currentLegal = currentInnings.legalBallsBowled
        val overNum = currentLegal / 6
        val ballNum = (currentLegal % 6) + (if (ball.isLegalDelivery) 1 else 0)

        val ballWithInnings = ball.copy(
            inningsNumber = inningsNum,
            overNumber = overNum,
            ballNumberInOver = ballNum,
            totalLegalBallsInInnings = currentLegal + (if (ball.isLegalDelivery) 1 else 0)
        )

        val maxWickets = if (currentInnings.battingTeamId == currentMatch.teamA.teamId) {
            if (currentMatch.teamA.players.size > 1) (currentMatch.teamA.players.size - 1).coerceAtMost(10) else 10
        } else {
            if (currentMatch.teamB.players.size > 1) (currentMatch.teamB.players.size - 1).coerceAtMost(10) else 10
        }

        val scoringResult = scoringEngine.processBall(
            currentInnings = currentInnings,
            ball = ballWithInnings,
            totalOversInMatch = currentMatch.totalOvers,
            maxWickets = maxWickets
        )

        // Save ball in local DB & sync queue
        val ballResult = scoringRepository.scoreBall(currentMatch.matchId, ballWithInnings)
        if (ballResult.isFailure) {
            return Result.failure(ballResult.exceptionOrNull() ?: Exception("Failed to record ball"))
        }

        var updatedFirstInnings = if (inningsNum == 1) scoringResult.updatedInnings else inn1
        var updatedSecondInnings = if (inningsNum == 2) scoringResult.updatedInnings else inn2

        var nextInningsNumber = inningsNum
        var updatedStatus = currentMatch.status
        var resultMessage = currentMatch.resultMessage
        var winnerTeamId = currentMatch.winnerTeamId

        val maxLegalBalls = currentMatch.totalOvers * 6

        if (inningsNum == 1) {
            val inn1LegalBalls = scoringResult.updatedInnings.legalBallsBowled
            val inn1Wickets = scoringResult.updatedInnings.wickets

            if (inn1LegalBalls >= maxLegalBalls || inn1Wickets >= maxWickets) {
                updatedFirstInnings = scoringResult.updatedInnings.copy(isCompleted = true)
                nextInningsNumber = 2
                val targetRuns = updatedFirstInnings.totalRuns + 1

                val batB = updatedFirstInnings.bowlingTeamId
                val bowlB = updatedFirstInnings.battingTeamId
                updatedSecondInnings = (updatedSecondInnings ?: Innings(2, batB, bowlB)).copy(
                    battingTeamId = batB,
                    bowlingTeamId = bowlB,
                    target = targetRuns
                )
                resultMessage = "Target: $targetRuns runs in ${currentMatch.totalOvers} overs"
            }
        } else if (inningsNum == 2) {
            val inn1Runs = updatedFirstInnings?.totalRuns ?: 0
            val targetRuns = updatedSecondInnings?.target ?: (inn1Runs + 1)
            val inn2Runs = scoringResult.updatedInnings.totalRuns
            val inn2LegalBalls = scoringResult.updatedInnings.legalBallsBowled
            val inn2Wickets = scoringResult.updatedInnings.wickets

            val isTargetReached = inn2Runs >= targetRuns
            val isOversOver = inn2LegalBalls >= maxLegalBalls
            val isAllOut = inn2Wickets >= maxWickets

            if (isTargetReached || isOversOver || isAllOut) {
                updatedSecondInnings = scoringResult.updatedInnings.copy(isCompleted = true)
                updatedStatus = MatchStatus.COMPLETED

                val batTeamName = if (updatedSecondInnings.battingTeamId == currentMatch.teamA.teamId) currentMatch.teamA.teamName else currentMatch.teamB.teamName
                val bowlTeamName = if (updatedSecondInnings.bowlingTeamId == currentMatch.teamA.teamId) currentMatch.teamA.teamName else currentMatch.teamB.teamName

                if (inn2Runs >= targetRuns) {
                    winnerTeamId = updatedSecondInnings.battingTeamId
                    val wktsLeft = maxWickets - inn2Wickets
                    resultMessage = "$batTeamName won by $wktsLeft wickets!"
                } else if (inn1Runs > inn2Runs) {
                    winnerTeamId = updatedFirstInnings?.battingTeamId
                    val runsDiff = inn1Runs - inn2Runs
                    resultMessage = "$bowlTeamName won by $runsDiff runs!"
                } else {
                    resultMessage = "Match Tied!"
                }
            }
        }

        val isMatchJustCompleted = (updatedStatus == MatchStatus.COMPLETED)
        val updatedMatchDate = if (isMatchJustCompleted && currentMatch.status != MatchStatus.COMPLETED) System.currentTimeMillis() else currentMatch.matchDate

        val updatedMatch = currentMatch.copy(
            firstInnings = updatedFirstInnings,
            secondInnings = updatedSecondInnings,
            currentInningsNumber = nextInningsNumber,
            currentStrikerId = scoringResult.nextStrikerId ?: currentMatch.currentStrikerId,
            currentNonStrikerId = scoringResult.nextNonStrikerId ?: currentMatch.currentNonStrikerId,
            status = updatedStatus,
            resultMessage = resultMessage,
            winnerTeamId = winnerTeamId,
            matchDate = updatedMatchDate,
            updatedAt = System.currentTimeMillis()
        )

        val syncMatchResult = scoringRepository.syncInningsState(currentMatch.matchId, updatedMatch)
        if (syncMatchResult.isFailure) {
            syncRepository.enqueueSyncTask(
                action = "SCORE_BALL",
                entityId = ballWithInnings.ballId,
                payloadJson = ballWithInnings.toString()
            )
        }

        return Result.success(scoringResult)
    }
}
