package com.cricpro.app.domain.engine

import com.cricpro.app.domain.model.*

class ScoringEngine {

    data class ScoringResult(
        val updatedInnings: Innings,
        val nextStrikerId: String?,
        val nextNonStrikerId: String?,
        val isOverComplete: Boolean,
        val isInningsComplete: Boolean
    )

    fun processBall(
        currentInnings: Innings,
        ball: Ball,
        totalOversInMatch: Int,
        maxWickets: Int = 10
    ): ScoringResult {
        val updatedBalls = currentInnings.ballsHistory + ball
        return recalculateInnings(
            balls = updatedBalls,
            battingTeamId = currentInnings.battingTeamId,
            bowlingTeamId = currentInnings.bowlingTeamId,
            inningsNumber = currentInnings.inningsNumber,
            target = currentInnings.target,
            totalOversInMatch = totalOversInMatch,
            maxWickets = maxWickets
        )
    }

    fun recalculateInnings(
        balls: List<Ball>,
        battingTeamId: String,
        bowlingTeamId: String,
        inningsNumber: Int,
        target: Int?,
        totalOversInMatch: Int,
        maxWickets: Int = 10
    ): ScoringResult {
        var totalRuns = 0
        var totalWickets = 0
        var totalLegalBalls = 0
        var widesCount = 0
        var noBallsCount = 0
        var byesCount = 0
        var legByesCount = 0

        val battersMap = mutableMapOf<String, BatterScore>()
        val bowlersMap = mutableMapOf<String, BowlerScore>()
        val fallOfWickets = mutableListOf<FallOfWicket>()
        val partnerships = mutableListOf<Partnership>()

        var currentPartnershipRuns = 0
        var currentPartnershipBalls = 0
        var p1Id: String? = null
        var p2Id: String? = null

        var activeStrikerId: String? = null
        var activeNonStrikerId: String? = null

        var currentOverLegalBalls = 0
        var currentOverBowlerRuns = 0
        var currentOverBowlerId: String? = null

        val maxLegalBalls = totalOversInMatch * 6

        for (ball in balls) {
            activeStrikerId = ball.strikerId
            activeNonStrikerId = ball.nonStrikerId

            if (p1Id == null) p1Id = ball.strikerId
            if (p2Id == null) p2Id = ball.nonStrikerId

            val isLegal = ball.isLegalDelivery
            val extraType = ball.extraType
            val runsScored = ball.runsScored
            val extraRuns = ball.extraRuns
            val totalBallRuns = runsScored + extraRuns

            totalRuns += totalBallRuns
            if (isLegal) {
                totalLegalBalls++
                currentOverLegalBalls++
                currentPartnershipBalls++
            }

            // Track extras
            when (extraType) {
                ExtraType.WIDE -> widesCount += extraRuns
                ExtraType.NO_BALL -> noBallsCount += extraRuns
                ExtraType.BYE -> byesCount += extraRuns
                ExtraType.LEG_BYE -> legByesCount += extraRuns
                ExtraType.NONE -> {}
            }

            // Batter Stats update
            val strikerKey = if (battersMap.containsKey(ball.strikerId)) {
                ball.strikerId
            } else {
                battersMap.values.find { it.name == ball.strikerId || it.playerId == ball.strikerId }?.playerId ?: ball.strikerId
            }

            val currentBatter = battersMap[strikerKey] ?: BatterScore(playerId = ball.strikerId, name = ball.strikerId)
            val updatedBatter = currentBatter.copy(
                name = if (currentBatter.name.isNotBlank()) currentBatter.name else ball.strikerId,
                runs = currentBatter.runs + runsScored,
                balls = if (extraType != ExtraType.WIDE) currentBatter.balls + 1 else currentBatter.balls,
                fours = if (runsScored == 4 && extraType == ExtraType.NONE) currentBatter.fours + 1 else currentBatter.fours,
                sixes = if (runsScored == 6 && extraType == ExtraType.NONE) currentBatter.sixes + 1 else currentBatter.sixes
            )
            battersMap[strikerKey] = updatedBatter

            // Ensure non-striker exists in battersMap
            if (ball.nonStrikerId.isNotBlank()) {
                val nonStrikerKey = if (battersMap.containsKey(ball.nonStrikerId)) {
                    ball.nonStrikerId
                } else {
                    battersMap.values.find { it.name == ball.nonStrikerId || it.playerId == ball.nonStrikerId }?.playerId ?: ball.nonStrikerId
                }
                if (!battersMap.containsKey(nonStrikerKey)) {
                    battersMap[nonStrikerKey] = BatterScore(playerId = ball.nonStrikerId, name = ball.nonStrikerId)
                }
            }

            // Bowler Stats update
            val bowlerKey = if (bowlersMap.containsKey(ball.bowlerId)) {
                ball.bowlerId
            } else {
                bowlersMap.values.find { it.name == ball.bowlerId || it.playerId == ball.bowlerId }?.playerId ?: ball.bowlerId
            }

            val currentBowler = bowlersMap[bowlerKey] ?: BowlerScore(playerId = ball.bowlerId, name = ball.bowlerId)
            val bowlerRunsThisBall = when (extraType) {
                ExtraType.BYE, ExtraType.LEG_BYE -> 0
                else -> totalBallRuns
            }
            currentOverBowlerRuns += bowlerRunsThisBall
            currentOverBowlerId = ball.bowlerId

            val updatedBowler = currentBowler.copy(
                name = if (currentBowler.name.isNotBlank()) currentBowler.name else ball.bowlerId,
                legalBalls = if (isLegal) currentBowler.legalBalls + 1 else currentBowler.legalBalls,
                runsConceded = currentBowler.runsConceded + bowlerRunsThisBall,
                wides = if (extraType == ExtraType.WIDE) currentBowler.wides + extraRuns else currentBowler.wides,
                noBalls = if (extraType == ExtraType.NO_BALL) currentBowler.noBalls + 1 else currentBowler.noBalls
            )
            bowlersMap[bowlerKey] = updatedBowler

            currentPartnershipRuns += totalBallRuns

            // Handle Wicket
            if (ball.wicketType != WicketType.NONE) {
                totalWickets++
                val rawDismissedId = ball.dismissedPlayerId ?: ball.strikerId
                val dismissedKey = if (battersMap.containsKey(rawDismissedId)) {
                    rawDismissedId
                } else {
                    battersMap.values.find { it.name == rawDismissedId || it.playerId == rawDismissedId }?.playerId ?: rawDismissedId
                }

                // Mark dismissed batter
                val dismissedBatter = battersMap[dismissedKey] ?: BatterScore(playerId = rawDismissedId, name = rawDismissedId)
                battersMap[dismissedKey] = dismissedBatter.copy(
                    name = if (dismissedBatter.name.isNotBlank()) dismissedBatter.name else rawDismissedId,
                    isOut = true,
                    dismissalInfo = "${ball.wicketType.name.lowercase()} b ${ball.bowlerId}"
                )

                // Bowler gets credit for wicket if not run out / retired out
                if (ball.wicketType != WicketType.RUN_OUT && ball.wicketType != WicketType.RETIRED_OUT) {
                    val wBowler = bowlersMap[bowlerKey]
                    if (wBowler != null) {
                        bowlersMap[bowlerKey] = wBowler.copy(wickets = wBowler.wickets + 1)
                    }
                }

                // Record Fall of Wicket
                val oversStr = "${totalLegalBalls / 6}.${totalLegalBalls % 6}"
                fallOfWickets.add(
                    FallOfWicket(
                        wicketNumber = totalWickets,
                        score = totalRuns,
                        oversFormatted = oversStr,
                        dismissedPlayerName = rawDismissedId
                    )
                )

                // Record Partnership
                partnerships.add(
                    Partnership(
                        player1Id = p1Id ?: ball.strikerId,
                        player2Id = p2Id ?: ball.nonStrikerId,
                        runs = currentPartnershipRuns,
                        balls = currentPartnershipBalls
                    )
                )
                currentPartnershipRuns = 0
                currentPartnershipBalls = 0
                p1Id = null
                p2Id = null
            }

            // Calculate strike rotation for this ball
            var swapStriker = false
            when (extraType) {
                ExtraType.WIDE -> {
                    // If additional runs scored on wide is odd
                    val additionalRunsOnWide = extraRuns - 1
                    if (additionalRunsOnWide % 2 != 0) swapStriker = true
                }
                ExtraType.NO_BALL, ExtraType.NONE -> {
                    if (runsScored % 2 != 0) swapStriker = true
                }
                ExtraType.BYE, ExtraType.LEG_BYE -> {
                    if (extraRuns % 2 != 0) swapStriker = true
                }
            }

            if (swapStriker && ball.wicketType == WicketType.NONE) {
                val temp = activeStrikerId
                activeStrikerId = activeNonStrikerId
                activeNonStrikerId = temp
            }

            // Over Completion check
            var isOverEnd = false
            if (currentOverLegalBalls == 6) {
                isOverEnd = true
                // Maiden check
                if (currentOverBowlerRuns == 0 && currentOverBowlerId != null) {
                    val mBowlerKey = bowlersMap.keys.find { it == currentOverBowlerId }
                        ?: bowlersMap.entries.find { it.value.name == currentOverBowlerId || it.value.playerId == currentOverBowlerId }?.key
                        ?: currentOverBowlerId
                    val mBowler = bowlersMap[mBowlerKey]
                    if (mBowler != null) {
                        bowlersMap[mBowlerKey] = mBowler.copy(maidens = mBowler.maidens + 1)
                    }
                }

                // Swap strike at end of over
                val temp = activeStrikerId
                activeStrikerId = activeNonStrikerId
                activeNonStrikerId = temp

                currentOverLegalBalls = 0
                currentOverBowlerRuns = 0
            }
        }

        // Check if innings complete
        val isTargetReached = target != null && totalRuns >= target
        val isAllOut = totalWickets >= maxWickets
        val isOversExhausted = totalLegalBalls >= maxLegalBalls
        val isInningsComplete = isTargetReached || isAllOut || isOversExhausted

        val isOverComplete = (totalLegalBalls > 0 && totalLegalBalls % 6 == 0 && balls.lastOrNull()?.isLegalDelivery == true)

        val updatedInnings = Innings(
            inningsNumber = inningsNumber,
            battingTeamId = battingTeamId,
            bowlingTeamId = bowlingTeamId,
            totalRuns = totalRuns,
            wickets = totalWickets,
            legalBallsBowled = totalLegalBalls,
            target = target,
            isCompleted = isInningsComplete,
            batters = battersMap,
            bowlers = bowlersMap,
            fallOfWickets = fallOfWickets,
            partnerships = partnerships,
            extras = ExtrasSummary(
                wides = widesCount,
                noBalls = noBallsCount,
                byes = byesCount,
                legByes = legByesCount
            ),
            ballsHistory = balls
        )

        return ScoringResult(
            updatedInnings = updatedInnings,
            nextStrikerId = activeStrikerId,
            nextNonStrikerId = activeNonStrikerId,
            isOverComplete = isOverComplete,
            isInningsComplete = isInningsComplete
        )
    }
}
