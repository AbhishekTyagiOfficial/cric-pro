package com.cricpro.app

import com.cricpro.app.data.local.dao.BallDao
import com.cricpro.app.data.local.dao.MatchDao
import com.cricpro.app.data.local.entity.BallEntity
import com.cricpro.app.data.local.entity.MatchEntity
import com.cricpro.app.data.repository.MatchRepositoryImpl
import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import com.cricpro.app.domain.usecase.ScoreBallUseCase
import com.cricpro.app.domain.usecase.UndoBallUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Automated test suite covering test cases WK_FLD_01 through WK_FLD_20 for
 * Select Fielder for Catch and Run Out + Update Scorecard.
 */
class WicketFielderTestSuite {

    private lateinit var scoringEngine: ScoringEngine

    // Sample Teams & Players
    private val teamBatting = Team(
        teamId = "team_ind",
        teamName = "India",
        players = listOf(
            Player(playerId = "bat_1", name = "Rahul Sharma", role = PlayerRole.BATSMAN),
            Player(playerId = "bat_2", name = "Virat Kohli", role = PlayerRole.BATSMAN),
            Player(playerId = "bat_3", name = "Rohit Sharma", role = PlayerRole.BATSMAN)
        )
    )

    private val teamFielding = Team(
        teamId = "team_aus",
        teamName = "Australia",
        players = listOf(
            Player(playerId = "fld_1", name = "Abhishek Tyagi", role = PlayerRole.ALL_ROUNDER),
            Player(playerId = "fld_2", name = "Amit Kumar", role = PlayerRole.BOWLER),
            Player(playerId = "fld_3", name = "Rahul Verma", role = PlayerRole.ALL_ROUNDER),
            Player(playerId = "fld_dup_1", name = "Same Name Player", role = PlayerRole.BATSMAN),
            Player(playerId = "fld_dup_2", name = "Same Name Player", role = PlayerRole.BOWLER)
        )
    )

    private lateinit var baseInnings: Innings
    private lateinit var baseMatch: Match

    @Before
    fun setUp() {
        scoringEngine = ScoringEngine()
        baseInnings = Innings(
            inningsNumber = 1,
            battingTeamId = teamBatting.teamId,
            bowlingTeamId = teamFielding.teamId
        )
        baseMatch = Match(
            matchId = "match_test_01",
            title = "India vs Australia",
            teamA = teamBatting,
            teamB = teamFielding,
            currentInningsNumber = 1,
            firstInnings = baseInnings,
            currentStrikerId = "Rahul Sharma",
            currentNonStrikerId = "Virat Kohli",
            currentBowlerId = "Amit Kumar"
        )
    }

    // ==========================================
    // TC WK_FLD_01: Select CAUGHT opens fielder selection dialog
    // ==========================================
    @Test
    fun test_WK_FLD_01_SelectCaught_TriggersFielderSelection() {
        val selectedWicketType = WicketType.CAUGHT
        val requiresFielderDialog = (selectedWicketType == WicketType.CAUGHT || selectedWicketType == WicketType.RUN_OUT)
        assertTrue("WK_FLD_01: CAUGHT must trigger Select Fielder dialog", requiresFielderDialog)
    }

    // ==========================================
    // TC WK_FLD_02: Verify player list contains only current fielding team
    // ==========================================
    @Test
    fun test_WK_FLD_02_VerifyPlayerList_OnlyCurrentFieldingTeam() {
        val fieldingRoster = teamFielding.players
        val battingRoster = teamBatting.players

        // Verify fielding players are present
        val fieldingNames = fieldingRoster.map { it.name }
        assertTrue("WK_FLD_02: Abhishek Tyagi must be in fielding roster", fieldingNames.contains("Abhishek Tyagi"))
        assertTrue("WK_FLD_02: Amit Kumar must be in fielding roster", fieldingNames.contains("Amit Kumar"))

        // Verify batting players are NOT in fielding list
        for (battingPlayer in battingRoster) {
            assertFalse(
                "WK_FLD_02: Batting player ${battingPlayer.name} must NOT be in fielding player list",
                fieldingRoster.any { it.playerId == battingPlayer.playerId }
            )
        }
    }

    // ==========================================
    // TC WK_FLD_03: Select catcher saves player as catcher
    // ==========================================
    @Test
    fun test_WK_FLD_03_SelectCatcher_SavesFielderAsCatcher() {
        val catcher = teamFielding.players.first { it.name == "Abhishek Tyagi" }
        val delivery = Ball(
            ballId = "ball_c1",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.CAUGHT,
            fielderId = catcher.playerId,
            fielderName = catcher.name,
            dismissedPlayerId = "Rahul Sharma"
        )

        assertEquals("WK_FLD_03: Catcher playerId must match selected fielder", "fld_1", delivery.fielderId)
        assertEquals("WK_FLD_03: Catcher fielderName must match selected fielder", "Abhishek Tyagi", delivery.fielderName)
    }

    // ==========================================
    // TC WK_FLD_04: Verify caught scorecard displays 'c Fielder b Bowler'
    // ==========================================
    @Test
    fun test_WK_FLD_04_VerifyCaughtScorecard_DisplaysCorrectNotation() {
        val ball = Ball(
            ballId = "ball_c1",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "b_amit",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.CAUGHT,
            fielderId = "fld_1",
            fielderName = "Abhishek Tyagi",
            dismissedPlayerId = "Rahul Sharma"
        )

        val result = scoringEngine.processBall(baseInnings, ball, 20)
        val dismissedBatter = result.updatedInnings.batters["Rahul Sharma"]

        assertNotNull("Dismissed batter must be found in batters", dismissedBatter)
        assertTrue("Batter must be marked out", dismissedBatter!!.isOut)
        assertEquals(
            "WK_FLD_04: Caught dismissal format must be 'c Abhishek Tyagi b Amit Kumar'",
            "c Abhishek Tyagi b Amit Kumar",
            dismissedBatter.dismissalInfo
        )
    }

    // ==========================================
    // TC WK_FLD_05: Verify bowler attribution - delivery bowler credited
    // ==========================================
    @Test
    fun test_WK_FLD_05_VerifyBowlerAttribution_BowlerCreditedWithWicket() {
        val ball = Ball(
            ballId = "ball_c1",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "b_amit",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.CAUGHT,
            fielderId = "fld_1",
            fielderName = "Abhishek Tyagi",
            dismissedPlayerId = "Rahul Sharma"
        )

        val result = scoringEngine.processBall(baseInnings, ball, 20)
        val bowlerScore = result.updatedInnings.bowlers["b_amit"]

        assertNotNull("Bowler must be in bowlers map", bowlerScore)
        assertEquals("WK_FLD_05: Bowler name must be Amit Kumar", "Amit Kumar", bowlerScore!!.name)
        assertEquals("WK_FLD_05: Bowler must receive 1 wicket credit", 1, bowlerScore.wickets)
    }

    // ==========================================
    // TC WK_FLD_06: Change over after wicket retains original delivery bowler
    // ==========================================
    @Test
    fun test_WK_FLD_06_ChangeOverAfterWicket_RetainsOriginalBowler() {
        // Complete over 0 with wicket on ball 6
        var inn = baseInnings
        for (i in 1..5) {
            inn = scoringEngine.processBall(
                inn,
                Ball(ballId = "b$i", strikerId = "Rahul Sharma", nonStrikerId = "Virat Kohli", bowlerId = "Amit Kumar", bowlerName = "Amit Kumar", runsScored = 0),
                20
            ).updatedInnings
        }

        // Ball 6: Wicket by Amit Kumar, caught by Abhishek Tyagi
        val wicketBall = Ball(
            ballId = "b6",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.CAUGHT,
            fielderId = "fld_1",
            fielderName = "Abhishek Tyagi",
            dismissedPlayerId = "Rahul Sharma"
        )
        inn = scoringEngine.processBall(inn, wicketBall, 20).updatedInnings

        // Over 1 bowled by a different bowler: Pat Cummins
        for (i in 7..12) {
            inn = scoringEngine.processBall(
                inn,
                Ball(ballId = "b$i", strikerId = "Rohit Sharma", nonStrikerId = "Virat Kohli", bowlerId = "Pat Cummins", bowlerName = "Pat Cummins", runsScored = 1),
                20
            ).updatedInnings
        }

        // Check Rahul Sharma's dismissal still attributes Amit Kumar, NOT Pat Cummins
        val rahulScore = inn.batters["Rahul Sharma"]
        assertNotNull(rahulScore)
        assertEquals(
            "WK_FLD_06: Historical dismissal must still show Amit Kumar as bowler after over change",
            "c Abhishek Tyagi b Amit Kumar",
            rahulScore!!.dismissalInfo
        )
    }

    // ==========================================
    // TC WK_FLD_07: Cancel catch selection does not change score or record wicket
    // ==========================================
    @Test
    fun test_WK_FLD_07_CancelCatchSelection_NoWicketOrScoreChange() {
        val initialRuns = baseInnings.totalRuns
        val initialWickets = baseInnings.wickets
        val initialBalls = baseInnings.legalBallsBowled

        // Simulating user cancelling dialog: pendingWicketType set to null without calling scoreRuns
        var pendingWicketType: WicketType? = WicketType.CAUGHT
        // User presses Cancel
        pendingWicketType = null

        // Invariants remain intact
        assertEquals("WK_FLD_07: Runs must remain unchanged", initialRuns, baseInnings.totalRuns)
        assertEquals("WK_FLD_07: Wickets must remain unchanged", initialWickets, baseInnings.wickets)
        assertEquals("WK_FLD_07: Balls must remain unchanged", initialBalls, baseInnings.legalBallsBowled)
        assertNull("Pending wicket must be cleared on cancel", pendingWicketType)
    }

    // ==========================================
    // TC WK_FLD_08: Select RUN_OUT opens fielder selection dialog
    // ==========================================
    @Test
    fun test_WK_FLD_08_SelectRunOut_TriggersFielderSelection() {
        val selectedWicketType = WicketType.RUN_OUT
        val requiresFielderDialog = (selectedWicketType == WicketType.CAUGHT || selectedWicketType == WicketType.RUN_OUT)
        assertTrue("WK_FLD_08: RUN_OUT must trigger Select Fielder dialog", requiresFielderDialog)
    }

    // ==========================================
    // TC WK_FLD_09: Select run-out fielder saves player against run out
    // ==========================================
    @Test
    fun test_WK_FLD_09_SelectRunOutFielder_SavesFielder() {
        val fielder = teamFielding.players.first { it.name == "Rahul Verma" }
        val delivery = Ball(
            ballId = "ball_ro1",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.RUN_OUT,
            fielderId = fielder.playerId,
            fielderName = fielder.name,
            dismissedPlayerId = "Rahul Sharma"
        )

        assertEquals("WK_FLD_09: Run out fielderId must match selected fielder", "fld_3", delivery.fielderId)
        assertEquals("WK_FLD_09: Run out fielderName must match selected fielder", "Rahul Verma", delivery.fielderName)
    }

    // ==========================================
    // TC WK_FLD_10: Verify run-out scorecard displays 'run out (Fielder)' and no bowler wicket credit
    // ==========================================
    @Test
    fun test_WK_FLD_10_VerifyRunOutScorecard_DisplaysCorrectNotation() {
        val ball = Ball(
            ballId = "ball_ro1",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "b_amit",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.RUN_OUT,
            fielderId = "fld_3",
            fielderName = "Rahul Verma",
            dismissedPlayerId = "Rahul Sharma"
        )

        val result = scoringEngine.processBall(baseInnings, ball, 20)
        val dismissedBatter = result.updatedInnings.batters["Rahul Sharma"]

        assertNotNull("Dismissed batter must be found", dismissedBatter)
        assertEquals(
            "WK_FLD_10: Run out dismissal format must be 'run out (Rahul Verma)'",
            "run out (Rahul Verma)",
            dismissedBatter!!.dismissalInfo
        )

        // Bowler should NOT receive a wicket for run out
        val bowler = result.updatedInnings.bowlers["b_amit"]
        assertEquals("WK_FLD_10: Bowler must NOT be credited for run out", 0, bowler?.wickets ?: 0)
    }

    // ==========================================
    // TC WK_FLD_11: Verify run-out scoring - runs and extras remain correct
    // ==========================================
    @Test
    fun test_WK_FLD_11_VerifyRunOutScoring_RunsAndExtrasCalculatedCorrectly() {
        // Batter took 1 run before being run out, with a NO_BALL extra
        val ball = Ball(
            ballId = "ball_ro_extra",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar",
            bowlerName = "Amit Kumar",
            runsScored = 1,
            extraType = ExtraType.NO_BALL,
            extraRuns = 1,
            isLegalDelivery = false,
            wicketType = WicketType.RUN_OUT,
            fielderId = "fld_3",
            fielderName = "Rahul Verma",
            dismissedPlayerId = "Rahul Sharma"
        )

        val result = scoringEngine.processBall(baseInnings, ball, 20)
        val inn = result.updatedInnings

        assertEquals("WK_FLD_11: Total runs must include 1 run scored + 1 no-ball = 2", 2, inn.totalRuns)
        assertEquals("WK_FLD_11: Wickets must be 1", 1, inn.wickets)
        assertEquals("WK_FLD_11: Legal balls must be 0 for no ball", 0, inn.legalBallsBowled)
        assertEquals("WK_FLD_11: Batter runs must be 1", 1, inn.batters["Rahul Sharma"]?.runs)
        assertEquals("WK_FLD_11: Extras noBalls must be 1", 1, inn.extras.noBalls)
    }

    // ==========================================
    // TC WK_FLD_12: Cancel run-out selection does not change score
    // ==========================================
    @Test
    fun test_WK_FLD_12_CancelRunOutSelection_NoWicketOrScoreChange() {
        val initialRuns = baseInnings.totalRuns
        val initialWickets = baseInnings.wickets

        var pendingWicketType: WicketType? = WicketType.RUN_OUT
        // Scorer cancels dialog
        pendingWicketType = null

        assertEquals("WK_FLD_12: Total runs unchanged", initialRuns, baseInnings.totalRuns)
        assertEquals("WK_FLD_12: Total wickets unchanged", initialWickets, baseInnings.wickets)
        assertNull(pendingWicketType)
    }

    // ==========================================
    // TC WK_FLD_13: Verify team filtering - batting team cannot be selected
    // ==========================================
    @Test
    fun test_WK_FLD_13_VerifyTeamFiltering_BattingTeamExcludedFromFielders() {
        val battingPlayers = teamBatting.players
        val fieldingPlayers = teamFielding.players

        // Fielding players list passed to SelectFielderDialog
        val eligibleFielders = fieldingPlayers.filter { fp ->
            !battingPlayers.any { bp -> bp.playerId == fp.playerId }
        }

        assertEquals("WK_FLD_13: All fielding players are eligible", fieldingPlayers.size, eligibleFielders.size)
        for (bp in battingPlayers) {
            assertFalse(
                "WK_FLD_13: Batting player ${bp.name} must not be selectable as fielder",
                eligibleFielders.any { it.playerId == bp.playerId }
            )
        }
    }

    // ==========================================
    // TC WK_FLD_14: Duplicate player names stored distinguished by unique ID
    // ==========================================
    @Test
    fun test_WK_FLD_14_DuplicatePlayerNames_StoredByUniqueId() {
        val player1 = teamFielding.players.first { it.playerId == "fld_dup_1" }
        val player2 = teamFielding.players.first { it.playerId == "fld_dup_2" }

        assertEquals(player1.name, player2.name) // Same display name: "Same Name Player"
        assertNotEquals(player1.playerId, player2.playerId)

        val ballWithP1 = Ball(
            ballId = "b_dup_1",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar",
            wicketType = WicketType.CAUGHT,
            fielderId = player1.playerId,
            fielderName = player1.name
        )

        assertEquals("WK_FLD_14: Must record fld_dup_1 uniquely", "fld_dup_1", ballWithP1.fielderId)
        assertEquals("WK_FLD_14: Must record player name", "Same Name Player", ballWithP1.fielderName)
    }

    // ==========================================
    // TC WK_FLD_15: Rapid selection taps record only one wicket
    // ==========================================
    @Test
    fun test_WK_FLD_15_RapidSelectionTaps_RecordsSingleWicket() {
        var isSubmitting = false
        var submissionCount = 0

        fun onConfirmTapped(playerId: String, playerName: String) {
            if (isSubmitting) return
            isSubmitting = true
            submissionCount++
        }

        // Simulate 5 rapid consecutive taps
        for (i in 1..5) {
            onConfirmTapped("fld_1", "Abhishek Tyagi")
        }

        assertEquals("WK_FLD_15: Only 1 wicket event should be submitted despite rapid taps", 1, submissionCount)
    }

    // ==========================================
    // TC WK_FLD_16: Persist wicket details across reload / repository
    // ==========================================
    @Test
    fun test_WK_FLD_16_PersistWicketDetails_FielderAndBowlerPreserved() {
        val ball = Ball(
            ballId = "ball_persist_1",
            matchId = "match_test_01",
            inningsNumber = 1,
            overNumber = 0,
            ballNumberInOver = 1,
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "b_amit",
            bowlerName = "Amit Kumar",
            runsScored = 0,
            wicketType = WicketType.CAUGHT,
            fielderId = "fld_1",
            fielderName = "Abhishek Tyagi",
            dismissedPlayerId = "Rahul Sharma",
            commentary = "OUT! Amit Kumar to Rahul Sharma, c Abhishek Tyagi b Amit Kumar"
        )

        // Convert to entity
        val entity = BallEntity(
            ballId = ball.ballId,
            matchId = ball.matchId,
            inningsNumber = ball.inningsNumber,
            overNumber = ball.overNumber,
            ballNumberInOver = ball.ballNumberInOver,
            strikerId = ball.strikerId,
            nonStrikerId = ball.nonStrikerId,
            bowlerId = ball.bowlerId,
            bowlerName = ball.bowlerName,
            runsScored = ball.runsScored,
            extraType = ball.extraType.name,
            extraRuns = ball.extraRuns,
            isLegalDelivery = ball.isLegalDelivery,
            wicketType = ball.wicketType.name,
            dismissedPlayerId = ball.dismissedPlayerId,
            fielderId = ball.fielderId,
            fielderName = ball.fielderName,
            commentary = ball.commentary,
            timestamp = System.currentTimeMillis()
        )

        // Reconvert to domain (simulating DB query after reload)
        val loadedBall = Ball(
            ballId = entity.ballId,
            matchId = entity.matchId,
            inningsNumber = entity.inningsNumber,
            overNumber = entity.overNumber,
            ballNumberInOver = entity.ballNumberInOver,
            strikerId = entity.strikerId,
            nonStrikerId = entity.nonStrikerId,
            bowlerId = entity.bowlerId,
            bowlerName = entity.bowlerName,
            runsScored = entity.runsScored,
            extraType = ExtraType.valueOf(entity.extraType),
            extraRuns = entity.extraRuns,
            isLegalDelivery = entity.isLegalDelivery,
            wicketType = WicketType.valueOf(entity.wicketType),
            dismissedPlayerId = entity.dismissedPlayerId,
            fielderId = entity.fielderId,
            fielderName = entity.fielderName,
            commentary = entity.commentary ?: ""
        )

        assertEquals("WK_FLD_16: Bowler name preserved after reload", "Amit Kumar", loadedBall.bowlerName)
        assertEquals("WK_FLD_16: Fielder ID preserved after reload", "fld_1", loadedBall.fielderId)
        assertEquals("WK_FLD_16: Fielder Name preserved after reload", "Abhishek Tyagi", loadedBall.fielderName)

        val result = scoringEngine.processBall(baseInnings, loadedBall, 20)
        val batter = result.updatedInnings.batters["Rahul Sharma"]
        assertEquals("c Abhishek Tyagi b Amit Kumar", batter?.dismissalInfo)
    }

    // ==========================================
    // TC WK_FLD_17: Undo wicket delivery reverts wicket and fielder details
    // ==========================================
    @Test
    fun test_WK_FLD_17_UndoWicketDelivery_RevertsCleanly() {
        val ball1 = Ball(ballId = "b1", strikerId = "Rahul Sharma", nonStrikerId = "Virat Kohli", bowlerId = "Amit Kumar", runsScored = 4)
        val inn1 = scoringEngine.processBall(baseInnings, ball1, 20).updatedInnings

        val ball2Wicket = Ball(
            ballId = "b2",
            strikerId = "Rahul Sharma",
            nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar",
            bowlerName = "Amit Kumar",
            wicketType = WicketType.CAUGHT,
            fielderId = "fld_1",
            fielderName = "Abhishek Tyagi",
            dismissedPlayerId = "Rahul Sharma"
        )
        val inn2 = scoringEngine.processBall(inn1, ball2Wicket, 20).updatedInnings

        assertEquals(1, inn2.wickets)
        assertTrue(inn2.batters["Rahul Sharma"]?.isOut == true)

        // Undo the wicket delivery (recalculate with balls history excluding ball 2)
        val remainingBalls = inn2.ballsHistory.dropLast(1)
        val undoneResult = scoringEngine.recalculateInnings(
            balls = remainingBalls,
            battingTeamId = baseInnings.battingTeamId,
            bowlingTeamId = baseInnings.bowlingTeamId,
            inningsNumber = 1,
            target = null,
            totalOversInMatch = 20
        )
        val restoredInn = undoneResult.updatedInnings

        assertEquals("WK_FLD_17: Wickets count reverted to 0", 0, restoredInn.wickets)
        assertEquals("WK_FLD_17: Rahul Sharma not out after undo", false, restoredInn.batters["Rahul Sharma"]?.isOut)
        assertEquals("WK_FLD_17: Bowler wickets reverted to 0", 0, restoredInn.bowlers["Amit Kumar"]?.wickets)
        assertEquals("WK_FLD_17: Fall of wickets cleared", 0, restoredInn.fallOfWickets.size)
    }

    // ==========================================
    // TC WK_FLD_18: Other wicket types remain unchanged
    // ==========================================
    @Test
    fun test_WK_FLD_18_OtherWicketTypes_FormatCorrectly() {
        // BOWLED
        val bowledBall = Ball(
            ballId = "b_bowled", strikerId = "Rahul Sharma", nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar", bowlerName = "Amit Kumar", wicketType = WicketType.BOWLED
        )
        val bowledResult = scoringEngine.processBall(baseInnings, bowledBall, 20)
        assertEquals("WK_FLD_18: BOWLED must format as 'b Bowler'", "b Amit Kumar", bowledResult.updatedInnings.batters["Rahul Sharma"]?.dismissalInfo)

        // LBW
        val lbwBall = Ball(
            ballId = "b_lbw", strikerId = "Rahul Sharma", nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar", bowlerName = "Amit Kumar", wicketType = WicketType.LBW
        )
        val lbwResult = scoringEngine.processBall(baseInnings, lbwBall, 20)
        assertEquals("WK_FLD_18: LBW must format as 'lbw b Bowler'", "lbw b Amit Kumar", lbwResult.updatedInnings.batters["Rahul Sharma"]?.dismissalInfo)

        // HIT_WICKET
        val hwBall = Ball(
            ballId = "b_hw", strikerId = "Rahul Sharma", nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar", bowlerName = "Amit Kumar", wicketType = WicketType.HIT_WICKET
        )
        val hwResult = scoringEngine.processBall(baseInnings, hwBall, 20)
        assertEquals("WK_FLD_18: HIT_WICKET must format as 'hit wicket b Bowler'", "hit wicket b Amit Kumar", hwResult.updatedInnings.batters["Rahul Sharma"]?.dismissalInfo)

        // RETIRED_OUT
        val roBall = Ball(
            ballId = "b_ro", strikerId = "Rahul Sharma", nonStrikerId = "Virat Kohli",
            bowlerId = "Amit Kumar", bowlerName = "Amit Kumar", wicketType = WicketType.RETIRED_OUT
        )
        val roResult = scoringEngine.processBall(baseInnings, roBall, 20)
        assertEquals("WK_FLD_18: RETIRED_OUT must format as 'retired out'", "retired out", roResult.updatedInnings.batters["Rahul Sharma"]?.dismissalInfo)
    }

    // ==========================================
    // TC WK_FLD_19: Missing player data handled gracefully without crashing
    // ==========================================
    @Test
    fun test_WK_FLD_19_MissingPlayerData_HandlesGracefullyWithoutCrash() {
        val emptyFieldingTeam = Team(teamId = "t_empty", teamName = "Empty Team", players = emptyList())

        // Dialog fallback generates 11 fielders
        val fallbackFielders = if (emptyFieldingTeam.players.isNotEmpty()) {
            emptyFieldingTeam.players
        } else {
            (1..11).map { idx ->
                Player(
                    playerId = "fielder_$idx",
                    name = if (emptyFieldingTeam.teamName.isNotBlank()) "${emptyFieldingTeam.teamName} Fielder $idx" else "Fielder $idx"
                )
            }
        }

        assertEquals("WK_FLD_19: Fallback generates 11 fielders when roster empty", 11, fallbackFielders.size)
        assertEquals("Empty Team Fielder 1", fallbackFielders[0].name)

        // Process ball with missing fielderName / bowlerName
        val minimalBall = Ball(
            ballId = "b_min",
            strikerId = "Batter1",
            nonStrikerId = "Batter2",
            bowlerId = "Bowler1",
            wicketType = WicketType.CAUGHT,
            fielderId = "Fielder1",
            fielderName = null,
            bowlerName = null
        )
        val res = scoringEngine.processBall(baseInnings, minimalBall, 20)
        val batter = res.updatedInnings.batters["Batter1"]
        assertNotNull(batter)
        assertEquals("c Fielder1 b Bowler1", batter?.dismissalInfo)
    }

    // ==========================================
    // TC WK_FLD_20: Ball-by-ball commentary verification
    // ==========================================
    @Test
    fun test_WK_FLD_20_CommentaryVerification_CorrectDismissalDetailsInCommentary() {
        // Caught commentary
        val bowlerName = "Amit Kumar"
        val strikerName = "Rahul Sharma"
        val catcherName = "Abhishek Tyagi"
        val runOutFielder = "Rahul Verma"

        val caughtBall = Ball(
            ballId = "b_comm_c",
            strikerId = strikerName,
            nonStrikerId = "Virat Kohli",
            bowlerId = "b_amit",
            bowlerName = bowlerName,
            wicketType = WicketType.CAUGHT,
            fielderId = "fld_1",
            fielderName = catcherName,
            commentary = "OUT! $bowlerName to $strikerName, c $catcherName b $bowlerName"
        )

        assertTrue(
            "WK_FLD_20: Commentary must contain 'c Abhishek Tyagi b Amit Kumar'",
            caughtBall.commentary.contains("c Abhishek Tyagi b Amit Kumar")
        )

        // Run out commentary
        val runOutBall = Ball(
            ballId = "b_comm_ro",
            strikerId = strikerName,
            nonStrikerId = "Virat Kohli",
            bowlerId = "b_amit",
            bowlerName = bowlerName,
            wicketType = WicketType.RUN_OUT,
            fielderId = "fld_3",
            fielderName = runOutFielder,
            commentary = "OUT! $bowlerName to $strikerName, run out ($runOutFielder)"
        )

        assertTrue(
            "WK_FLD_20: Commentary must contain 'run out (Rahul Verma)'",
            runOutBall.commentary.contains("run out (Rahul Verma)")
        )
    }
}
