package com.cricpro.app

import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BowlerSelectionRulesTest {

    private lateinit var scoringEngine: ScoringEngine

    @Before
    fun setUp() {
        scoringEngine = ScoringEngine()
    }

    private fun computePreviousCompletedBowlers(
        legalBalls: Int,
        ballsHistory: List<Ball>,
        bowlerCount: Int
    ): Set<String> {
        val completedOverCount = legalBalls / 6
        return if (completedOverCount > 0 && bowlerCount > 1) {
            val lastCompletedOverIndex = completedOverCount - 1
            val ballsInLastOver = ballsHistory.filter { it.overNumber == lastCompletedOverIndex }
            val bowlersFromOverNum = ballsInLastOver.map { it.bowlerId }.filter { it.isNotBlank() }.toSet()
            if (bowlersFromOverNum.isNotEmpty()) {
                bowlersFromOverNum
            } else {
                val targetLegalBallIndex = completedOverCount * 6
                var legalCount = 0
                var bowlerAtTarget: String? = null
                for (ball in ballsHistory) {
                    if (ball.isLegalDelivery) {
                        legalCount++
                        if (legalCount == targetLegalBallIndex) {
                            bowlerAtTarget = ball.bowlerId
                            break
                        }
                    }
                }
                if (!bowlerAtTarget.isNullOrBlank()) setOf(bowlerAtTarget) else emptySet()
            }
        } else {
            emptySet()
        }
    }

    @Test
    fun testStartOfInningsHasNoPreviousBowler() {
        val ballsHistory = emptyList<Ball>()
        val legalBalls = 0
        val prevBowlers = computePreviousCompletedBowlers(legalBalls, ballsHistory, bowlerCount = 11)

        assertTrue("At 0.0 overs, previous completed bowlers must be empty", prevBowlers.isEmpty())
        assertFalse("Current bowler Abhishek must not be restricted as previous bowler", prevBowlers.contains("Abhishek"))
    }

    @Test
    fun testMidFirstOverHasNoPreviousBowler() {
        var innings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2")
        // Bowl 3 balls in Over 0 by Abhishek
        for (i in 1..3) {
            val ball = Ball(
                ballId = "b_$i",
                inningsNumber = 1,
                overNumber = 0,
                ballNumberInOver = i,
                strikerId = "Ravi",
                nonStrikerId = "Yadav",
                bowlerId = "Abhishek",
                runsScored = 1,
                isLegalDelivery = true
            )
            innings = scoringEngine.processBall(innings, ball, totalOversInMatch = 8).updatedInnings
        }

        val prevBowlers = computePreviousCompletedBowlers(innings.legalBallsBowled, innings.ballsHistory, bowlerCount = 11)
        assertTrue("During 0.3 overs, no completed over exists, previous bowlers must be empty", prevBowlers.isEmpty())
    }

    @Test
    fun testOver1CompletedRestrictsOver1BowlerForOver2() {
        var innings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2")
        // Bowl full Over 0 (6 balls) by Abhishek
        for (i in 1..6) {
            val ball = Ball(
                ballId = "b_$i",
                inningsNumber = 1,
                overNumber = 0,
                ballNumberInOver = i,
                strikerId = "Ravi",
                nonStrikerId = "Yadav",
                bowlerId = "Abhishek",
                runsScored = 0,
                isLegalDelivery = true
            )
            innings = scoringEngine.processBall(innings, ball, totalOversInMatch = 8).updatedInnings
        }

        assertEquals(6, innings.legalBallsBowled)
        val prevBowlers = computePreviousCompletedBowlers(innings.legalBallsBowled, innings.ballsHistory, bowlerCount = 11)

        assertEquals(1, prevBowlers.size)
        assertTrue("Abhishek bowled Over 1, so he must be in previousBowlers for Over 2", prevBowlers.contains("Abhishek"))
        assertFalse("Aniket did not bowl Over 1, so he must NOT be in previousBowlers", prevBowlers.contains("Aniket"))
    }

    @Test
    fun testOver2MidOverChangeBowlerDoesNotRestrictCurrentBowler() {
        var innings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2")
        // Over 0: Abhishek
        for (i in 1..6) {
            val ball = Ball(
                ballId = "b_$i",
                inningsNumber = 1,
                overNumber = 0,
                ballNumberInOver = i,
                strikerId = "Ravi",
                nonStrikerId = "Yadav",
                bowlerId = "Abhishek",
                runsScored = 1,
                isLegalDelivery = true
            )
            innings = scoringEngine.processBall(innings, ball, totalOversInMatch = 8).updatedInnings
        }

        // Over 1 (Over 2 in 1-based): Aniket bowls 2 balls
        for (i in 1..2) {
            val ball = Ball(
                ballId = "b_${i + 6}",
                inningsNumber = 1,
                overNumber = 1,
                ballNumberInOver = i,
                strikerId = "Ravi",
                nonStrikerId = "Yadav",
                bowlerId = "Aniket",
                runsScored = 1,
                isLegalDelivery = true
            )
            innings = scoringEngine.processBall(innings, ball, totalOversInMatch = 8).updatedInnings
        }

        assertEquals(8, innings.legalBallsBowled)
        val prevBowlers = computePreviousCompletedBowlers(innings.legalBallsBowled, innings.ballsHistory, bowlerCount = 11)

        assertTrue("Abhishek bowled the completed previous over (Over 1), so he is restricted for Over 2", prevBowlers.contains("Abhishek"))
        assertFalse("Aniket is bowling the current over (Over 2), so he is NOT in previousBowlers", prevBowlers.contains("Aniket"))
    }

    @Test
    fun testOver2CompletedRestrictsAniketAndFreesAbhishekForOver3() {
        var innings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2")
        // Over 0: Abhishek (6 balls)
        for (i in 1..6) {
            val ball = Ball(
                ballId = "b_$i",
                inningsNumber = 1,
                overNumber = 0,
                ballNumberInOver = i,
                strikerId = "Ravi",
                nonStrikerId = "Yadav",
                bowlerId = "Abhishek",
                runsScored = 0,
                isLegalDelivery = true
            )
            innings = scoringEngine.processBall(innings, ball, totalOversInMatch = 8).updatedInnings
        }
        // Over 1: Aniket (6 balls)
        for (i in 1..6) {
            val ball = Ball(
                ballId = "b_${i + 6}",
                inningsNumber = 1,
                overNumber = 1,
                ballNumberInOver = i,
                strikerId = "Ravi",
                nonStrikerId = "Yadav",
                bowlerId = "Aniket",
                runsScored = 0,
                isLegalDelivery = true
            )
            innings = scoringEngine.processBall(innings, ball, totalOversInMatch = 8).updatedInnings
        }

        assertEquals(12, innings.legalBallsBowled)
        val prevBowlers = computePreviousCompletedBowlers(innings.legalBallsBowled, innings.ballsHistory, bowlerCount = 11)

        assertTrue("Aniket bowled Over 2, so he must be restricted for Over 3", prevBowlers.contains("Aniket"))
        assertFalse("Abhishek bowled Over 1 (not Over 2), so he is now FREE to bowl Over 3", prevBowlers.contains("Abhishek"))
    }

    @Test
    fun testSecondInningsHasNoPreviousBowlerAtStart() {
        // Even if Abhishek bowled in innings 1, innings 2 starts with legalBalls = 0 and empty ballsHistory
        val innings2 = Innings(inningsNumber = 2, battingTeamId = "t2", bowlingTeamId = "t1")
        val prevBowlers = computePreviousCompletedBowlers(innings2.legalBallsBowled, innings2.ballsHistory, bowlerCount = 11)

        assertTrue("Second innings start (0.0 overs) must have no previous bowlers", prevBowlers.isEmpty())
        assertFalse("Abhishek must not be restricted at start of Innings 2", prevBowlers.contains("Abhishek"))
    }

    @Test
    fun testSingleBowlerSquadAllowsSameBowlerConsecutively() {
        // If a squad only has 1 player, bowlerCount = 1, restriction is bypassed
        val balls = (1..6).map {
            Ball(
                ballId = "b_$it",
                inningsNumber = 1,
                overNumber = 0,
                ballNumberInOver = it,
                strikerId = "P1",
                nonStrikerId = "P2",
                bowlerId = "SoloBowler",
                runsScored = 1,
                isLegalDelivery = true
            )
        }
        val prevBowlers = computePreviousCompletedBowlers(legalBalls = 6, ballsHistory = balls, bowlerCount = 1)
        assertTrue("Single-player squad must not restrict the only bowler available", prevBowlers.isEmpty())
    }
}
