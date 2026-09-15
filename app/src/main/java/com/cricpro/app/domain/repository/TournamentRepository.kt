package com.cricpro.app.domain.repository

import com.cricpro.app.domain.model.Ground
import com.cricpro.app.domain.model.Tournament
import kotlinx.coroutines.flow.Flow

interface TournamentRepository {
    fun getTournaments(): Flow<List<Tournament>>
    fun getTournamentById(tournamentId: String): Flow<Tournament?>
    suspend fun createTournament(tournament: Tournament): Result<String>
    suspend fun updateTournament(tournament: Tournament): Result<Unit>
    suspend fun generateFixtures(tournamentId: String): Result<Unit>
    suspend fun recalculatePointsTable(tournamentId: String): Result<Unit>
}

interface GroundRepository {
    fun getGrounds(): Flow<List<Ground>>
    suspend fun createGround(ground: Ground): Result<String>
    suspend fun updateGround(ground: Ground): Result<Unit>
    suspend fun deleteGround(groundId: String): Result<Unit>
}

interface SyncRepository {
    suspend fun enqueueSyncTask(action: String, entityId: String, payloadJson: String): Result<Unit>
    suspend fun processPendingSyncQueue(): Result<Int>
}
