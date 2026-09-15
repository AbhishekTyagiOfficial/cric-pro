package com.cricpro.app.domain.repository

import com.cricpro.app.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getPlayerById(playerId: String): Flow<Player?>
    fun searchPlayers(query: String): Flow<List<Player>>
    suspend fun updatePlayerStats(playerId: String, runs: Int, wickets: Int, ballsFaced: Int): Result<Unit>
}
