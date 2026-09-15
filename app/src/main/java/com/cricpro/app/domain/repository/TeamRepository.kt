package com.cricpro.app.domain.repository

import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    fun getTeams(): Flow<List<Team>>
    fun getTeamById(teamId: String): Flow<Team?>
    suspend fun createTeam(team: Team): Result<String>
    suspend fun updateTeam(team: Team): Result<Unit>
    suspend fun deleteTeam(teamId: String): Result<Unit>
    
    // Player management within team
    suspend fun addPlayer(teamId: String, player: Player): Result<Unit>
    suspend fun updatePlayer(teamId: String, player: Player): Result<Unit>
    suspend fun removePlayer(teamId: String, playerId: String): Result<Unit>
    
    // Captain / Vice Captain atomic business rules
    suspend fun assignCaptain(teamId: String, playerId: String): Result<Unit>
    suspend fun assignViceCaptain(teamId: String, playerId: String): Result<Unit>
}
