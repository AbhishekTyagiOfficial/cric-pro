package com.cricpro.app.data.remote

import com.cricpro.app.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveTeam(team: Team): Result<Unit> {
        return try {
            firestore.collection("teams").document(team.teamId).set(team).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveMatch(match: Match): Result<Unit> {
        return try {
            firestore.collection("matches").document(match.matchId).set(match).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBall(matchId: String, ball: Ball): Result<Unit> {
        return try {
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

    suspend fun saveTournament(tournament: Tournament): Result<Unit> {
        return try {
            firestore.collection("tournaments").document(tournament.tournamentId).set(tournament).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
