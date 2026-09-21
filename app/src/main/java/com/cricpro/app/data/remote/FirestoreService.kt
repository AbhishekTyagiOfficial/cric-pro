package com.cricpro.app.data.remote

import com.cricpro.app.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveTeam(team: Team): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            firestore.collection("teams").document(team.teamId).set(team).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveMatch(match: Match): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val prunedMatch = match.copy(
                firstInnings = match.firstInnings?.copy(ballsHistory = emptyList()),
                secondInnings = match.secondInnings?.copy(ballsHistory = emptyList())
            )
            firestore.collection("matches").document(prunedMatch.matchId).set(prunedMatch).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBall(matchId: String, ball: Ball): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            firestore.collection("matches")
                .document(matchId)
                .collection("balls")
                .document(ball.ballId)
                .set(ball)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTournament(tournament: Tournament): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            firestore.collection("tournaments").document(tournament.tournamentId).set(tournament).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getPossibleUserIds(userId: String): List<String> {
        val result = mutableSetOf(userId)
        val clean = userId.trim().lowercase()
        if (clean.contains("@")) {
            val encoded = clean.replace(".", "_")
            result.add("user_$encoded")
            result.add("google_$encoded")
            result.add(clean)
        } else if (clean.startsWith("user_") || clean.startsWith("google_")) {
            val emailPart = clean.removePrefix("user_").removePrefix("google_")
            if (emailPart.contains("_")) {
                val lastUnderscore = emailPart.lastIndexOf("_")
                val restoredEmail = emailPart.substring(0, lastUnderscore) + "." + emailPart.substring(lastUnderscore + 1)
                result.add(restoredEmail)
            }
            result.add("user_$emailPart")
            result.add("google_$emailPart")
            result.add(emailPart)
        }
        return result.toList()
    }

    suspend fun getTeamsByOwner(ownerId: String): List<Team> = withContext(Dispatchers.IO) {
        if (ownerId.isBlank() || ownerId == "guest") return@withContext emptyList()
        return@withContext try {
            val ids = getPossibleUserIds(ownerId)
            val teamsList = mutableListOf<Team>()
            for (id in ids) {
                val snapshot = firestore.collection("teams")
                    .whereEqualTo("ownerId", id)
                    .get()
                    .await()
                teamsList.addAll(snapshot.toObjects(Team::class.java))
            }
            teamsList.distinctBy { it.teamId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMatchesByCreator(creatorId: String): List<Match> = withContext(Dispatchers.IO) {
        if (creatorId.isBlank() || creatorId == "guest") return@withContext emptyList()
        return@withContext try {
            val ids = getPossibleUserIds(creatorId)
            val matchesList = mutableListOf<Match>()
            for (id in ids) {
                val snapshot = firestore.collection("matches")
                    .whereEqualTo("creatorId", id)
                    .get()
                    .await()
                matchesList.addAll(snapshot.toObjects(Match::class.java))
            }
            matchesList.distinctBy { it.matchId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTournamentsByOrganizer(organizerId: String): List<Tournament> = withContext(Dispatchers.IO) {
        if (organizerId.isBlank() || organizerId == "guest") return@withContext emptyList()
        return@withContext try {
            val ids = getPossibleUserIds(organizerId)
            val tourList = mutableListOf<Tournament>()
            for (id in ids) {
                val snapshot = firestore.collection("tournaments")
                    .whereEqualTo("organizerId", id)
                    .get()
                    .await()
                tourList.addAll(snapshot.toObjects(Tournament::class.java))
            }
            tourList.distinctBy { it.tournamentId }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
