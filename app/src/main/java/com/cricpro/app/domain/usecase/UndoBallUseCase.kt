package com.cricpro.app.domain.usecase

import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.Ball
import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.repository.ScoringRepository
import javax.inject.Inject

class UndoBallUseCase @Inject constructor(
    private val scoringRepository: ScoringRepository,
    private val scoringEngine: ScoringEngine
) {
    suspend operator fun invoke(currentMatch: Match): Result<Match> {
        val inningsNumber = currentMatch.currentInningsNumber
        val currentInnings = if (inningsNumber == 1) currentMatch.firstInnings else currentMatch.secondInnings
        if (currentInnings == null) {
            return Result.failure(IllegalStateException("Innings not found"))
        }

        val balls = currentInnings.ballsHistory
        if (balls.isEmpty()) {
            return Result.failure(IllegalStateException("No balls to undo"))
        }

        val remainingBalls = balls.dropLast(1)
        val scoringResult = scoringEngine.recalculateInnings(
            balls = remainingBalls,
            battingTeamId = currentInnings.battingTeamId,
            bowlingTeamId = currentInnings.bowlingTeamId,
            inningsNumber = inningsNumber,
            target = currentInnings.target,
            totalOversInMatch = currentMatch.totalOvers
        )

        val updatedInnings = scoringResult.updatedInnings
        val updatedMatch = currentMatch.copy(
            firstInnings = if (inningsNumber == 1) updatedInnings else currentMatch.firstInnings,
            secondInnings = if (inningsNumber == 2) updatedInnings else currentMatch.secondInnings,
            currentStrikerId = scoringResult.nextStrikerId,
            currentNonStrikerId = scoringResult.nextNonStrikerId,
            updatedAt = System.currentTimeMillis()
        )

        scoringRepository.undoLastBall(currentMatch.matchId, inningsNumber)
        scoringRepository.syncInningsState(currentMatch.matchId, updatedMatch)

        return Result.success(updatedMatch)
    }
}

class EditBallUseCase @Inject constructor(
    private val scoringRepository: ScoringRepository,
    private val scoringEngine: ScoringEngine
) {
    suspend operator fun invoke(currentMatch: Match, editedBall: Ball): Result<Match> {
        val inningsNumber = currentMatch.currentInningsNumber
        val currentInnings = if (inningsNumber == 1) currentMatch.firstInnings else currentMatch.secondInnings
        if (currentInnings == null) {
            return Result.failure(IllegalStateException("Innings not found"))
        }

        val updatedBalls = currentInnings.ballsHistory.map { b ->
            if (b.ballId == editedBall.ballId) editedBall else b
        }

        val scoringResult = scoringEngine.recalculateInnings(
            balls = updatedBalls,
            battingTeamId = currentInnings.battingTeamId,
            bowlingTeamId = currentInnings.bowlingTeamId,
            inningsNumber = inningsNumber,
            target = currentInnings.target,
            totalOversInMatch = currentMatch.totalOvers
        )

        val updatedInnings = scoringResult.updatedInnings
        val updatedMatch = currentMatch.copy(
            firstInnings = if (inningsNumber == 1) updatedInnings else currentMatch.firstInnings,
            secondInnings = if (inningsNumber == 2) updatedInnings else currentMatch.secondInnings,
            currentStrikerId = scoringResult.nextStrikerId,
            currentNonStrikerId = scoringResult.nextNonStrikerId,
            updatedAt = System.currentTimeMillis()
        )

        scoringRepository.editBall(currentMatch.matchId, editedBall)
        scoringRepository.syncInningsState(currentMatch.matchId, updatedMatch)

        return Result.success(updatedMatch)
    }
}
