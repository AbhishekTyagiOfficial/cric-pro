package com.cricpro.app.domain.repository

import com.cricpro.app.domain.model.Ball
import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.model.TossDecision
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getRecentMatches(): Flow<List<Match>>
    fun getUpcomingMatches(): Flow<List<Match>>
    fun getCompletedMatches(): Flow<List<Match>>
    fun getMatchById(matchId: String): Flow<Match?>
    suspend fun createMatch(match: Match): Result<String>
    suspend fun updateMatchToss(matchId: String, tossWinnerId: String, decision: TossDecision): Result<Unit>
    suspend fun updatePlayingXI(matchId: String, playingXIA: List<String>, playingXIB: List<String>): Result<Unit>
    suspend fun updateMatchStatus(matchId: String, status: String): Result<Unit>
    suspend fun searchMatches(query: String): Flow<List<Match>>
}

interface ScoringRepository {
    fun getBallsForMatch(matchId: String, inningsNumber: Int): Flow<List<Ball>>
    suspend fun scoreBall(matchId: String, ball: Ball): Result<Unit>
    suspend fun undoLastBall(matchId: String, inningsNumber: Int): Result<Unit>
    suspend fun editBall(matchId: String, updatedBall: Ball): Result<Unit>
    suspend fun deleteBall(matchId: String, ballId: String): Result<Unit>
    suspend fun syncInningsState(matchId: String, match: Match): Result<Unit>
}
