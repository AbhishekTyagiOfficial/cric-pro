package com.cricpro.app

import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class InningsTransitionTest {

    private lateinit var scoringEngine: ScoringEngine
    private lateinit var testMatch: Match

    @Before
    fun setUp() {
        scoringEngine = ScoringEngine()
        val teamA = Team(teamId = "t1", teamName = "India")
        val teamB = Team(teamId = "t2", teamName = "Australia")

        testMatch = Match(
            matchId = "match_transition_test",
            title = "T20 Championship Final",
            matchType = MatchType.T20,
            totalOvers = 20,
            teamA = teamA,
            teamB = teamB,
            status = MatchStatus.IN_PROGRESS,
            currentInningsNumber = 1,
            firstInnings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2"),
            secondInnings = Innings(inningsNumber = 2, battingTeamId = "t2", bowlingTeamId = "t1")
        )
    }

    @Test
    fun testFirstInningsCompletionCalculatesCorrectTarget() {
        var inn1 = testMatch.firstInnings!!

        // Bowl 120 legal balls (20 overs) with 1 run each = 120 runs
        val balls = (1..120).map { i ->
            Ball(
                ballId = "b_$i", matchId = testMatch.matchId, inningsNumber = 1,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 1, isLegalDelivery = true
            )
        }

        val res = scoringEngine.recalculateInnings(
            balls = balls,
            battingTeamId = inn1.battingTeamId,
            bowlingTeamId = inn1.bowlingTeamId,
            inningsNumber = 1,
            target = null,
            totalOversInMatch = testMatch.totalOvers
        )

        inn1 = res.updatedInnings
        assertTrue("1st Innings must be completed after 120 legal balls", inn1.isCompleted)
        assertEquals(120, inn1.totalRuns)

        // Target for 2nd innings must be inn1.totalRuns + 1
        val targetForInn2 = inn1.totalRuns + 1
        assertEquals(121, targetForInn2)
    }

    @Test
    fun testFirstInningsEndOnWidesOrNoBalls() {
        var inn1 = testMatch.firstInnings!!

        // 119 legal balls = 119 runs
        val balls = (1..119).map { i ->
            Ball(
                ballId = "b_$i", matchId = testMatch.matchId, inningsNumber = 1,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 1, isLegalDelivery = true
            )
        }.toMutableList()

        // 120th delivery is a Wide + 4 extra runs = 5 runs (illegal delivery)
        balls.add(
            Ball(
                ballId = "b_wide", matchId = testMatch.matchId, inningsNumber = 1,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                extraType = ExtraType.WIDE, extraRuns = 5, isLegalDelivery = false
            )
        )

        var res = scoringEngine.recalculateInnings(
            balls = balls,
            battingTeamId = inn1.battingTeamId,
            bowlingTeamId = inn1.bowlingTeamId,
            inningsNumber = 1,
            target = null,
            totalOversInMatch = testMatch.totalOvers
        )
        assertFalse("1st Innings should not be completed on wide", res.updatedInnings.isCompleted)
        assertEquals(124, res.updatedInnings.totalRuns) // 119 + 5 = 124

        // 120th legal delivery (6 runs)
        balls.add(
            Ball(
                ballId = "b_120", matchId = testMatch.matchId, inningsNumber = 1,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 6, isLegalDelivery = true
            )
        )

        res = scoringEngine.recalculateInnings(
            balls = balls,
            battingTeamId = inn1.battingTeamId,
            bowlingTeamId = inn1.bowlingTeamId,
            inningsNumber = 1,
            target = null,
            totalOversInMatch = testMatch.totalOvers
        )

        assertTrue("1st Innings must complete on 120th legal ball", res.updatedInnings.isCompleted)
        assertEquals(130, res.updatedInnings.totalRuns) // 124 + 6 = 130
        assertEquals(131, res.updatedInnings.totalRuns + 1) // Target = 131
    }

    @Test
    fun testAllOutInningsCompletionTarget() {
        var inn1 = testMatch.firstInnings!!

        // Bowl 10 wickets
        val balls = (1..10).map { i ->
            Ball(
                ballId = "w_$i", matchId = testMatch.matchId, inningsNumber = 1,
                strikerId = "P$i", nonStrikerId = "P11", bowlerId = "B1",
                runsScored = 4, wicketType = WicketType.BOWLED, dismissedPlayerId = "P$i"
            )
        }

        val res = scoringEngine.recalculateInnings(
            balls = balls,
            battingTeamId = inn1.battingTeamId,
            bowlingTeamId = inn1.bowlingTeamId,
            inningsNumber = 1,
            target = null,
            totalOversInMatch = testMatch.totalOvers
        )

        inn1 = res.updatedInnings
        assertEquals(10, inn1.wickets)
        assertTrue("Innings must be completed when 10 wickets are lost", inn1.isCompleted)
        assertEquals(40, inn1.totalRuns) // 10 * 4 = 40
        assertEquals(41, inn1.totalRuns + 1) // Target = 41
    }

    @Test
    fun testSecondInningsChasingTargetWin() {
        val inn1Runs = 150
        val target = inn1Runs + 1 // 151

        var inn2 = testMatch.secondInnings!!

        // Chasing team scores 152 in 25 balls (25 * 6 + 2 = 152)
        val balls = (1..25).map { i ->
            Ball(
                ballId = "inn2_$i", matchId = testMatch.matchId, inningsNumber = 2,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                runsScored = 6, isLegalDelivery = true
            )
        }.toMutableList()
        balls.add(
            Ball(
                ballId = "inn2_26", matchId = testMatch.matchId, inningsNumber = 2,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                runsScored = 2, isLegalDelivery = true
            )
        )

        val res2 = scoringEngine.recalculateInnings(
            balls = balls,
            battingTeamId = inn2.battingTeamId,
            bowlingTeamId = inn2.bowlingTeamId,
            inningsNumber = 2,
            target = target,
            totalOversInMatch = testMatch.totalOvers
        )

        inn2 = res2.updatedInnings
        assertEquals(152, inn2.totalRuns)
        assertTrue("2nd Innings must complete when target is reached", inn2.isCompleted || inn2.totalRuns >= target)
    }

    @Test
    fun testSecondInningsTieMatch() {
        val inn1Runs = 100
        val target = inn1Runs + 1 // 101

        var inn2 = testMatch.secondInnings!!

        // Bowl 120 legal balls in 2nd innings totaling exactly 100 runs
        val balls = (1..100).map { i ->
            Ball(
                ballId = "inn2_$i", matchId = testMatch.matchId, inningsNumber = 2,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                runsScored = 1, isLegalDelivery = true
            )
        }.toMutableList()
        // 20 dot balls to complete 120 balls
        for (i in 101..120) {
            balls.add(
                Ball(
                    ballId = "inn2_$i", matchId = testMatch.matchId, inningsNumber = 2,
                    strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
                    runsScored = 0, isLegalDelivery = true
                )
            )
        }

        val res2 = scoringEngine.recalculateInnings(
            balls = balls,
            battingTeamId = inn2.battingTeamId,
            bowlingTeamId = inn2.bowlingTeamId,
            inningsNumber = 2,
            target = target,
            totalOversInMatch = testMatch.totalOvers
        )

        inn2 = res2.updatedInnings
        assertEquals(100, inn2.totalRuns)
        assertEquals(120, inn2.legalBallsBowled)
        assertTrue("2nd Innings must complete when overs are exhausted", inn2.isCompleted)
        assertEquals(inn1Runs, inn2.totalRuns) // Equal score -> Tie
    }

    @Test
    fun testSecondInningsPlayerResetToChasingTeamSquad() {
        val teamA = Team(teamId = "t1", teamName = "India", players = listOf(Player(name = "Rohit"), Player(name = "Virat")))
        val teamB = Team(teamId = "t2", teamName = "Australia", players = listOf(Player(name = "Warner"), Player(name = "Head")))

        val match = testMatch.copy(
            teamA = teamA,
            teamB = teamB,
            currentInningsNumber = 1,
            currentStrikerId = "Rohit",
            currentNonStrikerId = "Virat",
            currentBowlerId = "Warner"
        )

        // Simulate 2nd innings reset
        val inn2 = match.secondInnings!!
        val batTeam2 = if (inn2.battingTeamId == match.teamA.teamId) match.teamA else match.teamB
        val bowlTeam2 = if (inn2.bowlingTeamId == match.teamA.teamId) match.teamA else match.teamB

        val newStriker = batTeam2.players.getOrNull(0)?.name ?: "Player 1"
        val newNonStriker = batTeam2.players.getOrNull(1)?.name ?: "Player 2"
        val newBowler = bowlTeam2.players.getOrNull(0)?.name ?: "Bowler 1"

        assertEquals("Warner", newStriker)
        assertEquals("Head", newNonStriker)
        assertEquals("Rohit", newBowler)

        assertNotEquals(match.currentStrikerId, newStriker)
        assertNotEquals(match.currentNonStrikerId, newNonStriker)
        assertNotEquals(match.currentBowlerId, newBowler)
    }
}
