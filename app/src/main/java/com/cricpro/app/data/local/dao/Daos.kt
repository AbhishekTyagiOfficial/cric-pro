package com.cricpro.app.data.local.dao

import androidx.room.*
import com.cricpro.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams ORDER BY updatedAt DESC")
    fun getTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE teamId = :teamId")
    fun getTeamById(teamId: String): Flow<TeamEntity?>

    @Query("SELECT * FROM teams WHERE teamId = :teamId")
    suspend fun getTeamByIdDirect(teamId: String): TeamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Update
    suspend fun updateTeam(team: TeamEntity)

    @Query("DELETE FROM teams WHERE teamId = :teamId")
    suspend fun deleteTeam(teamId: String)
}

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE teamId = :teamId")
    fun getPlayersForTeam(teamId: String): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE playerId = :playerId")
    fun getPlayerById(playerId: String): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE name LIKE '%' || :query || '%'")
    fun searchPlayers(query: String): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("DELETE FROM players WHERE playerId = :playerId")
    suspend fun deletePlayer(playerId: String)

    @Query("DELETE FROM players WHERE teamId = :teamId")
    suspend fun deletePlayersForTeam(teamId: String)
}

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE UPPER(status) != 'COMPLETED' ORDER BY matchDate DESC")
    fun getMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE UPPER(status) = 'COMPLETED' ORDER BY matchDate DESC")
    fun getCompletedMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE matchId = :matchId")
    fun getMatchById(matchId: String): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE matchId = :matchId")
    suspend fun getMatchByIdDirect(matchId: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE matchId = :matchId")
    suspend fun deleteMatch(matchId: String)
}

@Dao
interface BallDao {
    @Query("SELECT * FROM balls WHERE matchId = :matchId AND inningsNumber = :inningsNumber ORDER BY timestamp ASC")
    fun getBallsForInnings(matchId: String, inningsNumber: Int): Flow<List<BallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBall(ball: BallEntity)

    @Query("DELETE FROM balls WHERE ballId = :ballId")
    suspend fun deleteBall(ballId: String)

    @Query("DELETE FROM balls WHERE matchId = :matchId AND inningsNumber = :inningsNumber AND ballId = (SELECT ballId FROM balls WHERE matchId = :matchId AND inningsNumber = :inningsNumber ORDER BY timestamp DESC LIMIT 1)")
    suspend fun deleteLastBall(matchId: String, inningsNumber: Int)
}

@Dao
interface TournamentDao {
    @Query("SELECT * FROM tournaments ORDER BY startDate DESC")
    fun getTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE tournamentId = :tournamentId")
    fun getTournamentById(tournamentId: String): Flow<TournamentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    suspend fun getPendingSyncQueue(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun remove(id: Long)
}
