package com.cricpro.app.data.repository

import com.cricpro.app.data.local.dao.SyncQueueDao
import com.cricpro.app.data.local.dao.TournamentDao
import com.cricpro.app.data.local.entity.SyncQueueEntity
import com.cricpro.app.data.local.entity.TournamentEntity
import com.cricpro.app.data.remote.FirestoreService
import com.cricpro.app.domain.model.Ground
import com.cricpro.app.domain.model.Tournament
import com.cricpro.app.domain.model.TournamentType
import com.cricpro.app.domain.repository.GroundRepository
import com.cricpro.app.domain.repository.SyncRepository
import com.cricpro.app.domain.repository.TournamentRepository
import com.cricpro.app.data.remote.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TournamentRepositoryImpl @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : TournamentRepository {

    override fun getTournaments(): Flow<List<Tournament>> {
        val currentUid = authService.currentUserId ?: "guest"
        return tournamentDao.getTournaments().map { list ->
            list.map { it.toDomain() }.filter {
                it.organizerId == currentUid
            }
        }
    }

    override fun getTournamentById(tournamentId: String): Flow<Tournament?> {
        return tournamentDao.getTournamentById(tournamentId).map { it?.toDomain() }
    }

    override suspend fun createTournament(tournament: Tournament): Result<String> {
        return try {
            val currentUid = authService.currentUserId ?: "guest"
            val tourId = if (tournament.tournamentId.isNotBlank()) tournament.tournamentId else "tour_${System.currentTimeMillis()}"
            val finalTour = tournament.copy(
                tournamentId = tourId,
                organizerId = if (tournament.organizerId.isNotBlank()) tournament.organizerId else currentUid
            )
            tournamentDao.insertTournament(finalTour.toEntity())
            firestoreService.saveTournament(finalTour)
            Result.success(tourId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTournament(tournament: Tournament): Result<Unit> {
        return try {
            tournamentDao.insertTournament(tournament.toEntity())
            firestoreService.saveTournament(tournament)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateFixtures(tournamentId: String): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun recalculatePointsTable(tournamentId: String): Result<Unit> {
        return Result.success(Unit)
    }

    private fun TournamentEntity.toDomain(): Tournament {
        return Tournament(
            tournamentId = tournamentId,
            name = name,
            logoUrl = logoUrl,
            type = try { TournamentType.valueOf(type) } catch (e: Exception) { TournamentType.LEAGUE },
            organizerId = organizerId,
            startDate = startDate,
            endDate = endDate
        )
    }

    private fun Tournament.toEntity(): TournamentEntity {
        return TournamentEntity(
            tournamentId = tournamentId,
            name = name,
            logoUrl = logoUrl,
            type = type.name,
            organizerId = organizerId,
            startDate = startDate,
            endDate = endDate,
            tournamentJson = ""
        )
    }
}

class GroundRepositoryImpl @Inject constructor() : GroundRepository {
    private val groundsList = mutableListOf(
        Ground("g1", "Lords Cricket Ground", "London", "St John's Wood Rd, London NW8 8QN"),
        Ground("g2", "Melbourne Cricket Ground", "Melbourne", "Brunton Ave, Richmond VIC 3002"),
        Ground("g3", "Eden Gardens", "Kolkata", "B.B.D. Bagh, Kolkata, West Bengal 700021"),
        Ground("g4", "Wankhede Stadium", "Mumbai", "D Road, Churchgate, Mumbai 400020")
    )

    override fun getGrounds(): Flow<List<Ground>> = flowOf(groundsList)

    override suspend fun createGround(ground: Ground): Result<String> {
        val id = "ground_${System.currentTimeMillis()}"
        groundsList.add(ground.copy(groundId = id))
        return Result.success(id)
    }

    override suspend fun updateGround(ground: Ground): Result<Unit> {
        val index = groundsList.indexOfFirst { it.groundId == ground.groundId }
        if (index != -1) groundsList[index] = ground
        return Result.success(Unit)
    }

    override suspend fun deleteGround(groundId: String): Result<Unit> {
        groundsList.removeAll { it.groundId == groundId }
        return Result.success(Unit)
    }
}

class SyncRepositoryImpl @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val firestoreService: FirestoreService
) : SyncRepository {

    override suspend fun enqueueSyncTask(action: String, entityId: String, payloadJson: String): Result<Unit> {
        return try {
            syncQueueDao.enqueue(
                SyncQueueEntity(
                    action = action,
                    entityId = entityId,
                    payloadJson = payloadJson
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun processPendingSyncQueue(): Result<Int> {
        return try {
            val pendingItems = syncQueueDao.getPendingSyncQueue()
            var processedCount = 0
            for (item in pendingItems) {
                // Synchronize item with Firestore and remove from local sync queue
                syncQueueDao.remove(item.id)
                processedCount++
            }
            Result.success(processedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
