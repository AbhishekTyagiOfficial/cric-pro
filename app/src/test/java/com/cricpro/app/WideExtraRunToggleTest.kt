package com.cricpro.app

import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WideExtraRunToggleTest {

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
    fun testToggleON_singleWide() {
        // When Wide Extra Run toggle is ON (default), extraRunsVal = 1
        val wideExtraRunEnabled = true
        val extraRunsVal = if (wideExtraRunEnabled) 1 else 0
        val wideBall = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 0, extraType = ExtraType.WIDE, extraRuns = extraRunsVal, isLegalDelivery = false
        )

        val res = scoringEngine.processBall(baseInnings, wideBall, 20)
        assertEquals("Total runs should be 1 when toggle is ON", 1, res.updatedInnings.totalRuns)
        assertEquals("Extras wide runs should be 1", 1, res.updatedInnings.extras.wides)
        assertEquals("Extras total should be 1", 1, res.updatedInnings.extras.total)
        assertEquals("Legal balls bowled should remain 0", 0, res.updatedInnings.legalBallsBowled)

        val bowler = res.updatedInnings.bowlers["B1"]
        assertNotNull(bowler)
        assertEquals("Bowler wides statistic should be 1", 1, bowler!!.wides)
        assertEquals("Bowler runs conceded should be 1", 1, bowler.runsConceded)
    }

    @Test
    fun testToggleOFF_singleWide() {
        // When Wide Extra Run toggle is OFF, extraRunsVal = 0
        val wideExtraRunEnabled = false
        val extraRunsVal = if (wideExtraRunEnabled) 1 else 0
        assertEquals(0, extraRunsVal)

        val wideBall = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 0, extraType = ExtraType.WIDE, extraRuns = extraRunsVal, isLegalDelivery = false
        )

        val res = scoringEngine.processBall(baseInnings, wideBall, 20)
        assertEquals("Total runs should be 0 when toggle is OFF", 0, res.updatedInnings.totalRuns)
        assertEquals("Extras wide runs should be 0", 0, res.updatedInnings.extras.wides)
        assertEquals("Extras total should be 0", 0, res.updatedInnings.extras.total)
        assertEquals("Legal balls bowled should remain 0", 0, res.updatedInnings.legalBallsBowled)

        val bowler = res.updatedInnings.bowlers["B1"]
        assertNotNull(bowler)
        assertEquals("Bowler wides count statistic should still be recorded as 1", 1, bowler!!.wides)
        assertEquals("Bowler runs conceded should be 0", 0, bowler.runsConceded)
    }

    @Test
    fun testToggleON_multipleWides() {
        var currentInn = baseInnings
        val wideExtraRunEnabled = true
        for (i in 1..3) {
            val extraRunsVal = if (wideExtraRunEnabled) 1 else 0
            val ball = Ball(
                ballId = "b$i", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                runsScored = 0, extraType = ExtraType.WIDE, extraRuns = extraRunsVal, isLegalDelivery = false
            )
            currentInn = scoringEngine.processBall(currentInn, ball, 20).updatedInnings
        }

        assertEquals("Total runs for 3 wides with toggle ON should be 3", 3, currentInn.totalRuns)
        assertEquals("Extras wide runs should be 3", 3, currentInn.extras.wides)
        assertEquals("Bowler wides statistic should be 3", 3, currentInn.bowlers["B1"]?.wides)
    }

    @Test
    fun testToggleOFF_multipleWides() {
        var currentInn = baseInnings
        val wideExtraRunEnabled = false
        for (i in 1..3) {
            val extraRunsVal = if (wideExtraRunEnabled) 1 else 0
            val ball = Ball(
                ballId = "b$i", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                runsScored = 0, extraType = ExtraType.WIDE, extraRuns = extraRunsVal, isLegalDelivery = false
            )
            currentInn = scoringEngine.processBall(currentInn, ball, 20).updatedInnings
        }

        assertEquals("Total runs for 3 wides with toggle OFF should be 0", 0, currentInn.totalRuns)
        assertEquals("Extras wide runs should be 0", 0, currentInn.extras.wides)
        assertEquals("Bowler wides count statistic should still be 3", 3, currentInn.bowlers["B1"]?.wides)
    }

    @Test
    fun testToggleON_wideWithAdditionalRuns() {
        // Wide + 2 additional runs (e.g. overthrows)
        val wideExtraRunEnabled = true
        val extraRunsVal = if (wideExtraRunEnabled) 1 else 0 // 1
        val additionalRuns = 2
        val ball = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = additionalRuns, extraType = ExtraType.WIDE, extraRuns = extraRunsVal, isLegalDelivery = false
        )

        val res = scoringEngine.processBall(baseInnings, ball, 20)
        assertEquals("Total runs should be 1 + 2 = 3", 3, res.updatedInnings.totalRuns)
        assertEquals("Extras wide should include 1 extra run", 1, res.updatedInnings.extras.wides)
        assertEquals("Bowler runs conceded should be 3", 3, res.updatedInnings.bowlers["B1"]?.runsConceded)
        assertEquals("Bowler wides count should be 1", 1, res.updatedInnings.bowlers["B1"]?.wides)
    }

    @Test
    fun testToggleOFF_wideWithAdditionalRuns() {
        // Wide + 2 additional runs with toggle OFF
        val wideExtraRunEnabled = false
        val extraRunsVal = if (wideExtraRunEnabled) 1 else 0 // 0
        val additionalRuns = 2
        val ball = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = additionalRuns, extraType = ExtraType.WIDE, extraRuns = extraRunsVal, isLegalDelivery = false
        )

        val res = scoringEngine.processBall(baseInnings, ball, 20)
        assertEquals("Total runs should be 0 + 2 = 2", 2, res.updatedInnings.totalRuns)
        assertEquals("Extras wide should be 0 extra runs", 0, res.updatedInnings.extras.wides)
        assertEquals("Bowler runs conceded should be 2", 2, res.updatedInnings.bowlers["B1"]?.runsConceded)
        assertEquals("Bowler wides count statistic should still be 1", 1, res.updatedInnings.bowlers["B1"]?.wides)
    }

    @Test
    fun testDynamicToggleStateChangeDuringMatch() {
        // First ball: Wide with toggle ON
        var wideExtraRunEnabled = true
        val ball1 = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 0, extraType = ExtraType.WIDE, extraRuns = if (wideExtraRunEnabled) 1 else 0, isLegalDelivery = false
        )
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)
        assertEquals(1, res1.updatedInnings.totalRuns)

        // Toggle changed to OFF mid-match
        wideExtraRunEnabled = false
        val ball2 = Ball(
            ballId = "b2", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 0, extraType = ExtraType.WIDE, extraRuns = if (wideExtraRunEnabled) 1 else 0, isLegalDelivery = false
        )
        val res2 = scoringEngine.processBall(res1.updatedInnings, ball2, 20)

        // Total score should now be 1 (first wide) + 0 (second wide) = 1
        assertEquals(1, res2.updatedInnings.totalRuns)
        assertEquals(1, res2.updatedInnings.extras.wides)
        assertEquals("Bowler wide count should reflect both wide deliveries = 2", 2, res2.updatedInnings.bowlers["B1"]?.wides)
    }

    @Test
    fun testUndoWideBall() {
        val ball1 = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 0, extraType = ExtraType.WIDE, extraRuns = 1, isLegalDelivery = false
        )
        val res1 = scoringEngine.processBall(baseInnings, ball1, 20)
        assertEquals(1, res1.updatedInnings.totalRuns)
        assertEquals(1, res1.updatedInnings.ballsHistory.size)

        // Undo last ball by recalculating history without ball1
        val historyAfterUndo = res1.updatedInnings.ballsHistory.dropLast(1)
        val resUndo = scoringEngine.recalculateInnings(
            balls = historyAfterUndo,
            battingTeamId = baseInnings.battingTeamId,
            bowlingTeamId = baseInnings.bowlingTeamId,
            inningsNumber = 1,
            target = null,
            totalOversInMatch = 20
        )

        assertEquals(0, resUndo.updatedInnings.totalRuns)
        assertEquals(0, resUndo.updatedInnings.extras.wides)
        assertEquals(0, resUndo.updatedInnings.ballsHistory.size)
    }

    @Test
    fun testWideToggleDoesNotAffectNoBallOrOtherExtras() {
        val wideExtraRunEnabled = false // Wide toggle OFF

        val noBall = Ball(
            ballId = "b1", strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 0, extraType = ExtraType.NO_BALL, extraRuns = 1, isLegalDelivery = false
        )
        val res1 = scoringEngine.processBall(baseInnings, noBall, 20)

        assertEquals("No-Ball extra run should still be 1 when Wide toggle is OFF", 1, res1.updatedInnings.totalRuns)
        assertEquals("Extras noBalls should be 1", 1, res1.updatedInnings.extras.noBalls)
        assertEquals("Extras wides should be 0", 0, res1.updatedInnings.extras.wides)
    }
}
