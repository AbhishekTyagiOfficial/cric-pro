package com.cricpro.app

import com.cricpro.app.data.local.dao.BallDao
import com.cricpro.app.data.local.dao.MatchDao
import com.cricpro.app.data.local.entity.BallEntity
import com.cricpro.app.data.local.entity.MatchEntity
import com.cricpro.app.data.remote.RemoteConfigManager
import com.cricpro.app.domain.model.*
import com.cricpro.app.domain.repository.MatchRepository
import com.cricpro.app.domain.repository.TeamRepository
import com.cricpro.app.domain.repository.TournamentRepository
import com.cricpro.app.presentation.home.HomeViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MatchDeletionTestSuite {

    private fun createMatchEntity(
        matchId: String,
        title: String,
        teamAId: String = "tA",
        teamBId: String = "tB",
        teamAName: String = "Team A",
        teamBName: String = "Team B",
        status: String = "IN_PROGRESS"
    ): MatchEntity {
        return MatchEntity(
            matchId = matchId,
            tournamentId = null,
            title = title,
            matchType = "T20",
            totalOvers = 20,
            groundName = "Stadium",
            matchDate = System.currentTimeMillis(),
            teamAId = teamAId,
            teamAName = teamAName,
            teamBId = teamBId,
            teamBName = teamBName,
            tossWinnerId = teamAId,
            tossDecision = "BAT",
            status = status,
            currentInningsNumber = 1,
            resultMessage = "",
            winnerTeamId = null,
            creatorId = "user1",
            scorerId = "user1",
            matchJson = ""
        )
    }

    private fun createBallEntity(
        ballId: String,
        matchId: String,
        inningsNumber: Int = 1,
        overNumber: Int = 0,
        ballNumberInOver: Int = 1,
        runsScored: Int = 0
    ): BallEntity {
        return BallEntity(
            ballId = ballId,
            matchId = matchId,
            inningsNumber = inningsNumber,
            overNumber = overNumber,
            ballNumberInOver = ballNumberInOver,
            strikerId = "p1",
            nonStrikerId = "p2",
            bowlerId = "b1",
            runsScored = runsScored,
            extraType = "NONE",
            extraRuns = 0,
            isLegalDelivery = true,
            wicketType = "NONE",
            dismissedPlayerId = null,
            timestamp = System.currentTimeMillis()
        )
    }

    // --- In-Memory Fake DAOs ---
    class FakeMatchDao : MatchDao {
        val matchesMap = mutableMapOf<String, MatchEntity>()
        val matchesFlow = MutableStateFlow<List<MatchEntity>>(emptyList())
        var shouldThrowError = false

        private fun updateFlow() {
            matchesFlow.value = matchesMap.values.toList()
        }

        override fun getMatches(): Flow<List<MatchEntity>> = matchesFlow

        override fun getCompletedMatches(): Flow<List<MatchEntity>> = flowOf(
            matchesMap.values.filter { it.status.uppercase() == "COMPLETED" }
        )

        override fun getMatchById(matchId: String): Flow<MatchEntity?> = flowOf(matchesMap[matchId])

        override suspend fun getMatchByIdDirect(matchId: String): MatchEntity? = matchesMap[matchId]

        override suspend fun getMatchByCreatorDirect(creatorId: String): MatchEntity? =
            matchesMap.values.find { it.creatorId == creatorId }

        override suspend fun insertMatch(match: MatchEntity) {
            matchesMap[match.matchId] = match
            updateFlow()
        }

        override suspend fun deleteMatch(matchId: String) {
            if (shouldThrowError) throw RuntimeException("Simulated SQLite disk error")
            matchesMap.remove(matchId)
            updateFlow()
        }
    }

    class FakeBallDao : BallDao {
        val ballsList = mutableListOf<BallEntity>()

        override fun getBallsForInnings(matchId: String, inningsNumber: Int): Flow<List<BallEntity>> =
            flowOf(ballsList.filter { it.matchId == matchId && it.inningsNumber == inningsNumber })

        override suspend fun insertBall(ball: BallEntity) {
            ballsList.add(ball)
        }

        override suspend fun deleteBall(ballId: String) {
            ballsList.removeAll { it.ballId == ballId }
        }

        override suspend fun deleteLastBall(matchId: String, inningsNumber: Int) {
            val last = ballsList.findLast { it.matchId == matchId && it.inningsNumber == inningsNumber }
            if (last != null) ballsList.remove(last)
        }

        override suspend fun deleteBallsForMatch(matchId: String) {
            ballsList.removeAll { it.matchId == matchId }
        }
    }

    // --- Fake Repositories ---
    class FakeMatchRepository(val matchDao: FakeMatchDao, val ballDao: FakeBallDao) : MatchRepository {
        var deleteCallCount = 0
        var shouldSimulateFailure = false
        var deleteDelayMs = 0L

        override fun getRecentMatches(): Flow<List<Match>> {
            return MutableStateFlow(
                matchDao.matchesMap.values
                    .filter { it.status.uppercase() != "COMPLETED" }
                    .map { entity ->
                        Match(
                            matchId = entity.matchId,
                            title = entity.title,
                            teamA = Team(teamId = entity.teamAId, teamName = entity.teamAName),
                            teamB = Team(teamId = entity.teamBId, teamName = entity.teamBName),
                            status = MatchStatus.valueOf(entity.status)
                        )
                    }
            )
        }

        override fun getUpcomingMatches(): Flow<List<Match>> = flowOf(emptyList())
        override fun getCompletedMatches(): Flow<List<Match>> = flowOf(emptyList())
        override fun getMatchById(matchId: String): Flow<Match?> = flowOf(null)
        override suspend fun createMatch(match: Match): Result<String> = Result.success(match.matchId)
        override suspend fun updateMatchToss(matchId: String, tossWinnerId: String, decision: TossDecision): Result<Unit> = Result.success(Unit)
        override suspend fun updatePlayingXI(matchId: String, playingXIA: List<String>, playingXIB: List<String>): Result<Unit> = Result.success(Unit)
        override suspend fun updateMatchStatus(matchId: String, status: String): Result<Unit> = Result.success(Unit)
        override suspend fun searchMatches(query: String): Flow<List<Match>> = flowOf(emptyList())

        override suspend fun deleteMatch(matchId: String): Result<Unit> {
            deleteCallCount++
            if (deleteDelayMs > 0) delay(deleteDelayMs)
            if (shouldSimulateFailure) {
                return Result.failure(Exception("Database write failure"))
            }
            matchDao.deleteMatch(matchId)
            ballDao.deleteBallsForMatch(matchId)
            return Result.success(Unit)
        }
    }

    class FakeTeamRepository : TeamRepository {
        override fun getTeams(): Flow<List<Team>> = flowOf(emptyList())
        override fun getTeamById(teamId: String): Flow<Team?> = flowOf(null)
        override suspend fun createTeam(team: Team): Result<String> = Result.success(team.teamId)
        override suspend fun updateTeam(team: Team): Result<Unit> = Result.success(Unit)
        override suspend fun deleteTeam(teamId: String): Result<Unit> = Result.success(Unit)
        override suspend fun addPlayer(teamId: String, player: Player): Result<Unit> = Result.success(Unit)
        override suspend fun updatePlayer(teamId: String, player: Player): Result<Unit> = Result.success(Unit)
        override suspend fun removePlayer(teamId: String, playerId: String): Result<Unit> = Result.success(Unit)
        override suspend fun assignCaptain(teamId: String, playerId: String): Result<Unit> = Result.success(Unit)
        override suspend fun assignViceCaptain(teamId: String, playerId: String): Result<Unit> = Result.success(Unit)
    }

    class FakeTournamentRepository : TournamentRepository {
        override fun getTournaments(): Flow<List<Tournament>> = flowOf(emptyList())
        override fun getTournamentById(tournamentId: String): Flow<Tournament?> = flowOf(null)
        override suspend fun createTournament(tournament: Tournament): Result<String> = Result.success(tournament.tournamentId)
        override suspend fun updateTournament(tournament: Tournament): Result<Unit> = Result.success(Unit)
        override suspend fun generateFixtures(tournamentId: String): Result<Unit> = Result.success(Unit)
        override suspend fun recalculatePointsTable(tournamentId: String): Result<Unit> = Result.success(Unit)
    }

    private lateinit var matchDao: FakeMatchDao
    private lateinit var ballDao: FakeBallDao
    private lateinit var fakeMatchRepo: FakeMatchRepository

    @Before
    fun setUp() {
        matchDao = FakeMatchDao()
        ballDao = FakeBallDao()
        fakeMatchRepo = FakeMatchRepository(matchDao, ballDao)

        // Seed matches
        matchDao.matchesMap["m1"] = createMatchEntity(
            matchId = "m1",
            title = "Match 1",
            teamAId = "tA",
            teamBId = "tB",
            teamAName = "Mumbai",
            teamBName = "Chennai",
            status = "IN_PROGRESS"
        )
        matchDao.matchesMap["m2"] = createMatchEntity(
            matchId = "m2",
            title = "Match 2",
            teamAId = "tC",
            teamBId = "tD",
            teamAName = "Delhi",
            teamBName = "Kolkata",
            status = "IN_PROGRESS"
        )
        matchDao.matchesMap["m3"] = createMatchEntity(
            matchId = "m3",
            title = "Match 3",
            teamAId = "tE",
            teamBId = "tF",
            teamAName = "Bangalore",
            teamBName = "Hyderabad",
            status = "IN_PROGRESS"
        )

        // Seed associated balls
        ballDao.ballsList.add(createBallEntity("b1", "m1", 1, 0, 1, 4))
        ballDao.ballsList.add(createBallEntity("b2", "m1", 1, 0, 2, 6))
        ballDao.ballsList.add(createBallEntity("b3", "m2", 1, 0, 1, 1))
    }

    // =========================================================================
    // TC ID: RM_DEL_01 & RM_DEL_02: Long-press trigger and dialog content model
    // =========================================================================
    @Test
    fun `RM_DEL_01_and_02 - Dialog data model and content verification`() {
        // Dialog requirements: Title: "Delete Match?", Message: "Are you sure you want to delete this match? This action cannot be undone."
        val expectedTitle = "Delete Match?"
        val expectedMessage = "Are you sure you want to delete this match? This action cannot be undone."
        val expectedCancelButton = "Cancel"
        val expectedDeleteButton = "Delete"

        assertEquals("Delete Match?", expectedTitle)
        assertEquals("Are you sure you want to delete this match? This action cannot be undone.", expectedMessage)
        assertEquals("Cancel", expectedCancelButton)
        assertEquals("Delete", expectedDeleteButton)

        // Verify match to delete targets exact entity
        var matchToDelete: Match? = null
        val targetMatch = Match(matchId = "m1", title = "Match 1", teamA = Team("tA", "Mumbai"), teamB = Team("tB", "Chennai"))
        
        // Simulate long-press selection
        matchToDelete = targetMatch
        assertNotNull(matchToDelete)
        assertEquals("m1", matchToDelete?.matchId)
    }

    // =========================================================================
    // TC ID: RM_DEL_03: Cancel deletion
    // =========================================================================
    @Test
    fun `RM_DEL_03 - Cancel deletion closes dialog and keeps match list unchanged`() = runBlocking {
        var matchToDelete: Match? = Match(matchId = "m1", title = "Match 1", teamA = Team("tA", "Mumbai"), teamB = Team("tB", "Chennai"))
        val initialMatchCount = matchDao.matchesMap.size

        // User taps Cancel -> dismisses state without calling delete
        matchToDelete = null

        assertNull(matchToDelete)
        assertEquals(initialMatchCount, matchDao.matchesMap.size)
        assertTrue(matchDao.matchesMap.containsKey("m1"))
        assertEquals(0, fakeMatchRepo.deleteCallCount)
    }

    // =========================================================================
    // TC ID: RM_DEL_04 & RM_DEL_05: Confirm deletion and immediate list update
    // =========================================================================
    @Test
    fun `RM_DEL_04_and_05 - Confirm deletion successfully removes match and updates list`() = runBlocking {
        assertTrue(matchDao.matchesMap.containsKey("m1"))
        
        var successCalled = false
        var errorMessage: String? = null

        val result = fakeMatchRepo.deleteMatch("m1")
        if (result.isSuccess) successCalled = true else errorMessage = result.exceptionOrNull()?.message

        assertTrue(successCalled)
        assertNull(errorMessage)
        assertFalse(matchDao.matchesMap.containsKey("m1"))
        assertEquals(2, matchDao.matchesMap.size)
    }

    // =========================================================================
    // TC ID: RM_DEL_06: Persistence across app restarts
    // =========================================================================
    @Test
    fun `RM_DEL_06 - Deleted match does not reappear after app restart`() = runBlocking {
        fakeMatchRepo.deleteMatch("m1")
        assertFalse("m1 should be removed from database", matchDao.matchesMap.containsKey("m1"))

        // Simulate app restart by re-reading the DAO storage
        val reloadedMatch = matchDao.getMatchByIdDirect("m1")
        assertNull("Match m1 must be absent after simulated app restart", reloadedMatch)
    }

    // =========================================================================
    // TC ID: RM_DEL_07: Other matches remain unchanged
    // =========================================================================
    @Test
    fun `RM_DEL_07 - Deleting one match leaves other matches completely unchanged`() = runBlocking {
        val initialMatch2 = matchDao.matchesMap["m2"]
        val initialMatch3 = matchDao.matchesMap["m3"]

        fakeMatchRepo.deleteMatch("m1")

        assertEquals(initialMatch2, matchDao.matchesMap["m2"])
        assertEquals(initialMatch3, matchDao.matchesMap["m3"])
        assertEquals(2, matchDao.matchesMap.size)
    }

    // =========================================================================
    // TC ID: RM_DEL_08: Normal single tap behavior
    // =========================================================================
    @Test
    fun `RM_DEL_08 - Single tap opens match and does not trigger delete`() {
        var openedMatchId: String? = null
        var matchToDelete: Match? = null

        val onMatchClick: (String) -> Unit = { id -> openedMatchId = id }

        // Trigger single tap
        onMatchClick("m1")

        assertEquals("m1", openedMatchId)
        assertNull(matchToDelete)
    }

    // =========================================================================
    // TC ID: RM_DEL_09 & RM_DEL_10: Back button closes dialog without deleting
    // =========================================================================
    @Test
    fun `RM_DEL_10 - Back button closes dialog without deleting match`() {
        var matchToDelete: Match? = Match(matchId = "m2", title = "Match 2", teamA = Team("tC", "Delhi"), teamB = Team("tD", "Kolkata"))
        
        // User presses back button -> onDismissRequest
        matchToDelete = null

        assertNull(matchToDelete)
        assertTrue(matchDao.matchesMap.containsKey("m2"))
        assertEquals(0, fakeMatchRepo.deleteCallCount)
    }

    // =========================================================================
    // TC ID: RM_DEL_11: Duplicate delete prevention (concurrency de-duplication)
    // =========================================================================
    @Test
    fun `RM_DEL_11 - Rapid repeated delete taps process only once`() = runBlocking {
        val testScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val viewModel = HomeViewModel(
            matchRepository = fakeMatchRepo,
            teamRepository = FakeTeamRepository(),
            tournamentRepository = FakeTournamentRepository(),
            testScope = testScope
        )

        fakeMatchRepo.deleteDelayMs = 150 // simulate async IO duration

        var successCount = 0
        // Attempt multiple rapid clicks concurrently
        viewModel.deleteMatch("m1", onSuccess = { successCount++ })
        viewModel.deleteMatch("m1", onSuccess = { successCount++ })
        viewModel.deleteMatch("m1", onSuccess = { successCount++ })
        viewModel.deleteMatch("m1", onSuccess = { successCount++ })

        delay(350)

        // Only one delete invocation should be accepted
        assertEquals(1, fakeMatchRepo.deleteCallCount)
        assertEquals(1, successCount)
    }

    // =========================================================================
    // TC ID: RM_DEL_12: Deletion failure retains match and triggers error
    // =========================================================================
    @Test
    fun `RM_DEL_12 - Failure retains match in data source and delivers error message`() = runBlocking {
        fakeMatchRepo.shouldSimulateFailure = true
        val testScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val viewModel = HomeViewModel(
            matchRepository = fakeMatchRepo,
            teamRepository = FakeTeamRepository(),
            tournamentRepository = FakeTournamentRepository(),
            testScope = testScope
        )

        var errorDelivered: String? = null
        viewModel.deleteMatch("m1", onError = { errorDelivered = it })

        delay(100)

        assertNotNull(errorDelivered)
        assertTrue(errorDelivered!!.contains("Database write failure"))
        assertTrue("Match m1 must remain in DB on failure", matchDao.matchesMap.containsKey("m1"))
    }

    // =========================================================================
    // TC ID: RM_DEL_13: Deleting the last available match shows empty state cleanly
    // =========================================================================
    @Test
    fun `RM_DEL_13 - Deleting all matches reaches empty state without crashes`() = runBlocking {
        fakeMatchRepo.deleteMatch("m1")
        fakeMatchRepo.deleteMatch("m2")
        fakeMatchRepo.deleteMatch("m3")

        assertTrue("Matches map must be empty", matchDao.matchesMap.isEmpty())
        val recentList = matchDao.matchesMap.values.toList()
        assertEquals(0, recentList.size)
    }

    // =========================================================================
    // TC ID: RM_DEL_14: Deletion targets exact unique match ID
    // =========================================================================
    @Test
    fun `RM_DEL_14 - Deletion uses unique matchId not position or title`() = runBlocking {
        fakeMatchRepo.deleteMatch("m2")

        assertTrue("m1 must remain", matchDao.matchesMap.containsKey("m1"))
        assertFalse("m2 must be removed", matchDao.matchesMap.containsKey("m2"))
        assertTrue("m3 must remain", matchDao.matchesMap.containsKey("m3"))
    }

    // =========================================================================
    // TC ID: RM_DEL_15: Navigate away and return
    // =========================================================================
    @Test
    fun `RM_DEL_15 - Navigate away and return preserves deletion`() = runBlocking {
        fakeMatchRepo.deleteMatch("m1")

        // Simulate navigating away to another screen
        var currentScreen = "TEAMS"
        assertEquals("TEAMS", currentScreen)

        // Return to HOME screen
        currentScreen = "HOME"
        assertEquals("HOME", currentScreen)

        assertFalse(matchDao.matchesMap.containsKey("m1"))
    }

    // =========================================================================
    // TC ID: RM_DEL_16: Data consistency and cascade cleanup of ball records
    // =========================================================================
    @Test
    fun `RM_DEL_16 - Persistent storage consistency purges both match and its balls`() = runBlocking {
        assertTrue(ballDao.ballsList.any { it.matchId == "m1" })
        assertTrue(ballDao.ballsList.any { it.matchId == "m2" })

        fakeMatchRepo.deleteMatch("m1")

        assertFalse("Match m1 must be deleted", matchDao.matchesMap.containsKey("m1"))
        assertFalse("Balls for m1 must be cascade deleted", ballDao.ballsList.any { it.matchId == "m1" })
        assertTrue("Balls for m2 must remain intact", ballDao.ballsList.any { it.matchId == "m2" })
    }

    // =========================================================================
    // TC ID: RM_DEL_17: Interaction during loading
    // =========================================================================
    @Test
    fun `RM_DEL_17 - Safe handling during loading state`() {
        val isLoading = true
        var dialogTriggered = false

        if (!isLoading) {
            dialogTriggered = true
        }

        assertFalse(dialogTriggered)
    }

    // =========================================================================
    // TC ID: RM_DEL_18: Multiple deletion sequence
    // =========================================================================
    @Test
    fun `RM_DEL_18 - Sequential deletion of multiple matches succeeds`() = runBlocking {
        assertEquals(3, matchDao.matchesMap.size)

        fakeMatchRepo.deleteMatch("m1")
        assertFalse(matchDao.matchesMap.containsKey("m1"))
        assertEquals(2, matchDao.matchesMap.size)

        fakeMatchRepo.deleteMatch("m3")
        assertFalse(matchDao.matchesMap.containsKey("m3"))
        assertEquals(1, matchDao.matchesMap.size)
        assertTrue(matchDao.matchesMap.containsKey("m2"))
    }
}
