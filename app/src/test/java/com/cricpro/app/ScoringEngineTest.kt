package com.cricpro.app

import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ScoringEngineTest {

    private lateinit var scoringEngine: ScoringEngine
    private lateinit var baseInnings: Innings

    @Before
    fun setUp() {
        scoringEngine = ScoringEngine()
        baseInnings = Innings(
            inningsNumber = 1,
            battingTeamId = "TeamA",
            bowlingTeamId = "TeamB"
        )
    }

    @Test
    fun testDotBallAndSingle() {
        val ball1 = Ball(ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0)
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)

        assertEquals(0, res1.updatedInnings.totalRuns)
        assertEquals(1, res1.updatedInnings.legalBallsBowled)
        assertEquals("P1", res1.nextStrikerId)

        val ball2 = Ball(ballId = "b2", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 1)
        val res2 = scoringEngine.processBall(res1.updatedInnings, ball2, 20)

        assertEquals(1, res2.updatedInnings.totalRuns)
        assertEquals(2, res2.updatedInnings.legalBallsBowled)
        assertEquals("P2", res2.nextStrikerId) // Strike swapped on single
    }

    @Test
    fun testBoundariesFourAndSix() {
        val ball1 = Ball(ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 4)
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)
        assertEquals(4, res1.updatedInnings.totalRuns)
        assertEquals("P1", res1.nextStrikerId) // Strike unchanged on 4

        val ball2 = Ball(ballId = "b2", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 6)
        val res2 = scoringEngine.processBall(res1.updatedInnings, ball2, 20)
        assertEquals(10, res2.updatedInnings.totalRuns)
        assertEquals("P1", res2.nextStrikerId) // Strike unchanged on 6
    }

    @Test
    fun testExtrasWideAndNoBall() {
        val ball1 = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            extraType = ExtraType.WIDE, extraRuns = 1, isLegalDelivery = false
        )
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)
        assertEquals(1, res1.updatedInnings.totalRuns)
        assertEquals(0, res1.updatedInnings.legalBallsBowled) // Wide does not increment legal balls

        val ball2 = Ball(
            ballId = "b2", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 1, extraType = ExtraType.NO_BALL, extraRuns = 1, isLegalDelivery = false
        )
        val res2 = scoringEngine.processBall(res1.updatedInnings, ball2, 20)
        assertEquals(3, res2.updatedInnings.totalRuns) // 1 wide + 1 no-ball + 1 run scored = 3
        assertEquals(0, res2.updatedInnings.legalBallsBowled) // No Ball does not increment legal balls
    }

    @Test
    fun testWicketBowledAndCaught() {
        val ball1 = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            wicketType = WicketType.BOWLED, dismissedPlayerId = "P1"
        )
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)

        assertEquals(1, res1.updatedInnings.wickets)
        assertEquals(1, res1.updatedInnings.fallOfWickets.size)
        assertEquals(1, res1.updatedInnings.bowlers["B1"]?.wickets)
    }

    @Test
    fun testOverCompletionStrikeChange() {
        var inn = baseInnings
        for (i in 1..6) {
            val b = Ball(ballId = "b$i", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0)
            inn = scoringEngine.processBall(inn, b, 20).updatedInnings
        }

        assertEquals(6, inn.legalBallsBowled)
        assertEquals(1, inn.bowlers["B1"]?.maidens) // Maiden over!
    }

    @Test
    fun testUndoAndRecalculateInnings() {
        val b1 = Ball(ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 4)
        val b2 = Ball(ballId = "b2", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 6)
        val inn1 = scoringEngine.processBall(baseInnings, b1, 20).updatedInnings
        val inn2 = scoringEngine.processBall(inn1, b2, 20).updatedInnings

        assertEquals(10, inn2.totalRuns)

        // Undo ball 2 by recalculating with only [b1]
        val recalculated = scoringEngine.recalculateInnings(
            balls = listOf(b1),
            battingTeamId = "TeamA",
            bowlingTeamId = "TeamB",
            inningsNumber = 1,
            target = null,
            totalOversInMatch = 20
        )

        assertEquals(4, recalculated.updatedInnings.totalRuns)
        assertEquals(1, recalculated.updatedInnings.legalBallsBowled)
    }

    @Test
    fun testOverCompletionTriggersIsOverCompleteFlag() {
        var inn = baseInnings
        for (i in 1..5) {
            val b = Ball(ballId = "b$i", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0, isLegalDelivery = true)
            val res = scoringEngine.processBall(inn, b, 20)
            assertFalse(res.isOverComplete)
            inn = res.updatedInnings
        }
        val b6 = Ball(ballId = "b6", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0, isLegalDelivery = true)
        val res6 = scoringEngine.processBall(inn, b6, 20)
        assertTrue("6th legal ball must trigger isOverComplete = true", res6.isOverComplete)
    }

    @Test
    fun testWicketBatterScoreNameAndIsOut() {
        val wicketBall = Ball(
            ballId = "bw1", strikerId = "Himanshu", nonStrikerId = "Vinay", bowlerId = "abhi",
            wicketType = WicketType.BOWLED, dismissedPlayerId = "Himanshu"
        )
        val res = scoringEngine.processBall(baseInnings, wicketBall, 20)
        val hScore = res.updatedInnings.batters["Himanshu"]

        assertNotNull(hScore)
        assertEquals("Himanshu", hScore?.name)
        assertTrue(hScore?.isOut == true)
    }

    @Test
    fun testOddRunsStrikeRotation() {
        val b1 = Ball(ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 1)
        val res1 = scoringEngine.processBall(baseInnings, b1, 20)
        assertEquals("P2", res1.nextStrikerId) // 1 run swaps strike

        val b3 = Ball(ballId = "b3", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 3)
        val res3 = scoringEngine.processBall(baseInnings, b3, 20)
        assertEquals("P2", res3.nextStrikerId) // 3 runs swaps strike

        val b5 = Ball(ballId = "b5", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 5)
        val res5 = scoringEngine.processBall(baseInnings, b5, 20)
        assertEquals("P2", res5.nextStrikerId) // 5 runs swaps strike
    }

    @Test
    fun testVideoSequence() {
        // P1 = ekansh, P2 = abhi
        // Ball 1: 1 run
        val b1 = Ball(ballId = "b1", strikerId = "ekansh", nonStrikerId = "abhi", bowlerId = "yadav", runsScored = 1)
        val res1 = scoringEngine.processBall(baseInnings, b1, 2)
        // res1.nextStrikerId should be "abhi"

        // Ball 2: 1 run (faced by abhi)
        val b2 = Ball(ballId = "b2", strikerId = res1.nextStrikerId!!, nonStrikerId = res1.nextNonStrikerId!!, bowlerId = "yadav", runsScored = 1)
        val res2 = scoringEngine.processBall(res1.updatedInnings, b2, 2)
        // res2.nextStrikerId should be "ekansh"

        // Ball 3: 3 runs (faced by ekansh)
        val b3 = Ball(ballId = "b3", strikerId = res2.nextStrikerId!!, nonStrikerId = res2.nextNonStrikerId!!, bowlerId = "yadav", runsScored = 3)
        val res3 = scoringEngine.processBall(res2.updatedInnings, b3, 2)
        // res3.nextStrikerId should be "abhi"

        // Ball 4: 5 runs (faced by abhi)
        val b4 = Ball(ballId = "b4", strikerId = res3.nextStrikerId!!, nonStrikerId = res3.nextNonStrikerId!!, bowlerId = "yadav", runsScored = 5)
        val res4 = scoringEngine.processBall(res3.updatedInnings, b4, 2)
        // res4.nextStrikerId should be "ekansh"

        // Ball 5: 1 run (faced by ekansh)
        val b5 = Ball(ballId = "b5", strikerId = res4.nextStrikerId!!, nonStrikerId = res4.nextNonStrikerId!!, bowlerId = "yadav", runsScored = 1)
        val res5 = scoringEngine.processBall(res4.updatedInnings, b5, 2)

        println("AFTER BALL 4 (5 runs): nextStriker = ${res4.nextStrikerId}, nextNonStriker = ${res4.nextNonStrikerId}")
        println("AFTER BALL 5 (1 run): nextStriker = ${res5.nextStrikerId}, nextNonStriker = ${res5.nextNonStrikerId}")

        assertEquals("abhi", res5.nextStrikerId)
        assertEquals("ekansh", res5.nextNonStrikerId)
    }

    @Test
    fun testWicketIncomingBatterSelectionPersistence() {
        val wicketBall = Ball(
            ballId = "bw1", strikerId = "Vinay", nonStrikerId = "Himanshu", bowlerId = "ekansh",
            wicketType = WicketType.BOWLED, dismissedPlayerId = "Vinay"
        )
        val res = scoringEngine.processBall(baseInnings, wicketBall, 2)
        val vScore = res.updatedInnings.batters["Vinay"]
        val hScore = res.updatedInnings.batters["Himanshu"]

        assertTrue(vScore?.isOut == true)
        assertFalse(hScore?.isOut == true)

        // Dismissed engineStriker is "Vinay", which isOut = true.
        // User selects "sudeep" as non-dismissed incoming striker.
        val selectedStriker = "sudeep"
        val isSelectedOut = res.updatedInnings.batters[selectedStriker]?.isOut == true
        assertFalse("User selected batter sudeep is not out", isSelectedOut)
    }

    @Test
    fun testBatterScoreNameAndIdConsolidation() {
        // Ball 1 scored by player ID "p1"
        val ball1 = Ball(ballId = "b1", strikerId = "p1", nonStrikerId = "p2", bowlerId = "bw1", runsScored = 4)
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)

        // Ball 2 scored by player Name "p1"
        val ball2 = Ball(ballId = "b2", strikerId = "p1", nonStrikerId = "p2", bowlerId = "bw1", runsScored = 2)
        val res2 = scoringEngine.processBall(res1.updatedInnings, ball2, 20)

        val batterScore = res2.updatedInnings.batters["p1"]
        assertNotNull("Batter score should exist for p1", batterScore)
        assertEquals("Total runs for p1 should accumulate to 6", 6, batterScore?.runs)
        assertEquals("Total balls for p1 should accumulate to 2", 2, batterScore?.balls)
    }

    @Test
    fun testNonStrikerMapPresence() {
        val ball = Ball(ballId = "b1", strikerId = "StrikerA", nonStrikerId = "NonStrikerB", bowlerId = "Bowler1", runsScored = 2)
        val res = scoringEngine.processBall(baseInnings, ball, 20)

        val nonStrikerScore = res.updatedInnings.batters["NonStrikerB"]
        assertNotNull("Non-striker NonStrikerB should be present in batters map", nonStrikerScore)
        assertEquals("Non-striker runs should be 0", 0, nonStrikerScore?.runs)
        assertEquals("Non-striker balls should be 0", 0, nonStrikerScore?.balls)
    }
}


