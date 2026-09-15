package com.cricpro.app

import com.cricpro.app.data.repository.toDomain
import com.cricpro.app.data.repository.toEntity
import com.cricpro.app.domain.engine.ScoringEngine
import com.cricpro.app.domain.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MatchOversAndCompletionTest {

    private lateinit var scoringEngine: ScoringEngine
    private lateinit var testMatch2Overs: Match

    @Before
    fun setUp() {
        scoringEngine = ScoringEngine()
        val teamA = Team(teamId = "teamA", teamName = "India")
        val teamB = Team(teamId = "teamB", teamName = "Australia")

        testMatch2Overs = Match(
            matchId = "test_match_2o",
            title = "T2 Match",
            matchType = MatchType.CUSTOM,
            totalOvers = 2,
            teamA = teamA,
            teamB = teamB,
            status = MatchStatus.IN_PROGRESS,
            currentInningsNumber = 1,
            firstInnings = Innings(inningsNumber = 1, battingTeamId = "teamA", bowlingTeamId = "teamB"),
            secondInnings = Innings(inningsNumber = 2, battingTeamId = "teamB", bowlingTeamId = "teamA")
        )
    }

    @Test
    fun testFirstInningsOversLimitCompletionAt12LegalBalls() {
        var currentInnings = testMatch2Overs.firstInnings!!

        // Bowl 12 legal balls (2 overs)
        for (i in 1..12) {
            val ball = Ball(
                ballId = "ball_$i",
                matchId = testMatch2Overs.matchId,
                inningsNumber = 1,
                strikerId = "P1",
                nonStrikerId = "P2",
                bowlerId = "B1",
                runsScored = 1,
                isLegalDelivery = true
            )
            val result = scoringEngine.processBall(currentInnings, ball, testMatch2Overs.totalOvers)
            currentInnings = result.updatedInnings
        }

        assertEquals(12, currentInnings.legalBallsBowled)
        assertEquals(12, currentInnings.totalRuns)
        assertTrue(currentInnings.isCompleted)
    }

    @Test
    fun testWidesAndNoBallsDoNotCountTowardsLegalOversLimit() {
        var currentInnings = testMatch2Overs.firstInnings!!

        // Bowl 1 Wide delivery + 1 No Ball delivery (2 extra deliveries)
        val wideBall = Ball(
            ballId = "wide_1", matchId = testMatch2Overs.matchId, inningsNumber = 1,
            strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            extraType = ExtraType.WIDE, extraRuns = 1, isLegalDelivery = false
        )
        currentInnings = scoringEngine.processBall(currentInnings, wideBall, testMatch2Overs.totalOvers).updatedInnings

        val noBall = Ball(
            ballId = "noball_1", matchId = testMatch2Overs.matchId, inningsNumber = 1,
            strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1",
            runsScored = 1, extraType = ExtraType.NO_BALL, extraRuns = 1, isLegalDelivery = false
        )
        currentInnings = scoringEngine.processBall(currentInnings, noBall, testMatch2Overs.totalOvers).updatedInnings

        assertEquals(0, currentInnings.legalBallsBowled)
        assertEquals(3, currentInnings.totalRuns) // 1 wide + (1 nb + 1 run) = 3 runs
        assertFalse(currentInnings.isCompleted)

        // Bowl 11 legal balls (total 11 legal balls)
        for (i in 1..11) {
            val ball = Ball(
                ballId = "legal_$i", matchId = testMatch2Overs.matchId, inningsNumber = 1,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0, isLegalDelivery = true
            )
            currentInnings = scoringEngine.processBall(currentInnings, ball, testMatch2Overs.totalOvers).updatedInnings
        }

        assertEquals(11, currentInnings.legalBallsBowled)
        assertFalse(currentInnings.isCompleted)

        // 12th legal ball
        val finalBall = Ball(
            ballId = "legal_12", matchId = testMatch2Overs.matchId, inningsNumber = 1,
            strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0, isLegalDelivery = true
        )
        currentInnings = scoringEngine.processBall(currentInnings, finalBall, testMatch2Overs.totalOvers).updatedInnings

        assertEquals(12, currentInnings.legalBallsBowled)
        assertTrue(currentInnings.isCompleted)
    }

    @Test
    fun testAllOut10WicketsCompletesInningsEarly() {
        var currentInnings = testMatch2Overs.firstInnings!!

        // Bowl 10 wicket balls
        for (i in 1..10) {
            val ball = Ball(
                ballId = "w_$i", matchId = testMatch2Overs.matchId, inningsNumber = 1,
                strikerId = "P$i", nonStrikerId = "P11", bowlerId = "B1",
                wicketType = WicketType.BOWLED, dismissedPlayerId = "P$i"
            )
            currentInnings = scoringEngine.processBall(currentInnings, ball, testMatch2Overs.totalOvers).updatedInnings
        }

        assertEquals(10, currentInnings.wickets)
        assertEquals(10, currentInnings.legalBallsBowled) // 10 legal balls
        assertTrue(currentInnings.isCompleted)
    }

    @Test
    fun testSecondInningsTargetReached() {
        val inn1 = testMatch2Overs.firstInnings!!.copy(
            totalRuns = 20,
            legalBallsBowled = 12,
            isCompleted = true
        )

        var inn2 = testMatch2Overs.secondInnings!!.copy(
            target = 21
        )

        // Bowl 4 sixes in 2nd innings (24 runs >= target 21)
        for (i in 1..4) {
            val ball = Ball(
                ballId = "inn2_$i", matchId = testMatch2Overs.matchId, inningsNumber = 2,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 6, isLegalDelivery = true
            )
            inn2 = scoringEngine.processBall(inn2, ball, testMatch2Overs.totalOvers).updatedInnings
        }

        assertEquals(24, inn2.totalRuns)
        assertEquals(4, inn2.legalBallsBowled)
        assertTrue(inn2.isCompleted)
    }

    @Test
    fun testSecondInningsDefendTargetWinByRuns() {
        val inn1 = testMatch2Overs.firstInnings!!.copy(totalRuns = 30, legalBallsBowled = 12, isCompleted = true)
        var inn2 = testMatch2Overs.secondInnings!!.copy(target = 31)

        // Bowl 12 dot balls (0 runs scored)
        for (i in 1..12) {
            val ball = Ball(
                ballId = "dot_$i", matchId = testMatch2Overs.matchId, inningsNumber = 2,
                strikerId = "P1", nonStrikerId = "P2", bowlerId = "B1", runsScored = 0, isLegalDelivery = true
            )
            inn2 = scoringEngine.processBall(inn2, ball, testMatch2Overs.totalOvers).updatedInnings
        }

        assertEquals(0, inn2.totalRuns)
        assertEquals(12, inn2.legalBallsBowled)
        assertTrue(inn2.isCompleted)
    }

    @Test
    fun testMatchStatusFilteringMutuallyExclusive() {
        val inProgressMatch = testMatch2Overs.copy(status = MatchStatus.IN_PROGRESS)
        val completedMatch = testMatch2Overs.copy(status = MatchStatus.COMPLETED)
        val matches = listOf(inProgressMatch, completedMatch)

        val activeRecentMatches = matches.filter { it.status != MatchStatus.COMPLETED }
        val completedMatches = matches.filter { it.status == MatchStatus.COMPLETED }

        assertEquals(1, activeRecentMatches.size)
        assertEquals(MatchStatus.IN_PROGRESS, activeRecentMatches.first().status)

        assertEquals(1, completedMatches.size)
        assertEquals(MatchStatus.COMPLETED, completedMatches.first().status)
    }

    @Test
    fun testEntityToDomainScorePreservation() {
        val inn1 = Innings(1, "teamA", "teamB", totalRuns = 196, wickets = 3, legalBallsBowled = 64, isCompleted = true)
        val inn2 = Innings(2, "teamB", "teamA", totalRuns = 12, wickets = 0, legalBallsBowled = 2, target = 197, isCompleted = false)
        val matchWithScores = testMatch2Overs.copy(firstInnings = inn1, secondInnings = inn2)

        val entity = matchWithScores.toEntity()
        val domain = entity.toDomain()

        assertNotNull(domain.firstInnings)
        assertEquals(196, domain.firstInnings?.totalRuns)
        assertEquals(3, domain.firstInnings?.wickets)

        assertNotNull(domain.secondInnings)
        assertEquals(12, domain.secondInnings?.totalRuns)
        assertEquals(0, domain.secondInnings?.wickets)
        assertEquals(197, domain.secondInnings?.target)
    }

    @Test
    fun testEntityToDomainPlayerSelectionPreservation() {
        val matchWithPlayers = testMatch2Overs.copy(
            currentStrikerId = "vinay",
            currentNonStrikerId = "Yadav",
            currentBowlerId = "Himanshu"
        )
        val entity = matchWithPlayers.toEntity()
        val domain = entity.toDomain()

        assertEquals("vinay", domain.currentStrikerId)
        assertEquals("Yadav", domain.currentNonStrikerId)
        assertEquals("Himanshu", domain.currentBowlerId)
    }

    @Test
    fun testLastCompletedMatchComesFirstInCompletedMatches() {
        val olderCompletedMatch = testMatch2Overs.copy(
            matchId = "m1",
            status = MatchStatus.COMPLETED,
            matchDate = 1000L
        )
        val recentlyCompletedMatch = testMatch2Overs.copy(
            matchId = "m2",
            status = MatchStatus.COMPLETED,
            matchDate = 5000L
        )

        val list = listOf(olderCompletedMatch, recentlyCompletedMatch)
        val sortedList = list.sortedByDescending { it.matchDate }

        assertEquals("m2", sortedList.first().matchId)
        assertEquals("m1", sortedList.last().matchId)
    }
}
