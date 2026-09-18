package com.cricpro.app.data.repository

import com.cricpro.app.data.local.dao.PlayerDao
import com.cricpro.app.data.local.dao.TeamDao
import com.cricpro.app.data.local.entity.PlayerEntity
import com.cricpro.app.data.local.entity.TeamEntity
import com.cricpro.app.data.remote.FirestoreService
import com.cricpro.app.domain.model.BattingStyle
import com.cricpro.app.domain.model.BowlingStyle
import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.PlayerRole
import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import com.cricpro.app.data.remote.FirebaseAuthService
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(
    private val teamDao: TeamDao,
    private val playerDao: PlayerDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : TeamRepository {

    override fun getTeams(): Flow<List<Team>> {
        val currentUid = authService.currentUserId ?: "guest"
        return teamDao.getTeams().combine(playerDao.searchPlayers("")) { teamEntities, playerEntities ->
            teamEntities
                .filter { it.ownerId.isBlank() || it.ownerId == currentUid }
                .map { teamEntity ->
                    val teamPlayers = playerEntities.filter { it.teamId == teamEntity.teamId }.map { it.toDomain() }
                    teamEntity.toDomain(teamPlayers)
                }
        }
    }

    override fun getTeamById(teamId: String): Flow<Team?> {
        return teamDao.getTeamById(teamId).combine(playerDao.getPlayersForTeam(teamId)) { teamEntity, playerEntities ->
            teamEntity?.toDomain(playerEntities.map { it.toDomain() })
        }
    }

    override suspend fun createTeam(team: Team): Result<String> {
        return try {
            val teamId = if (team.teamId.isNotBlank()) team.teamId else "team_${System.currentTimeMillis()}"
            val currentOwner = team.ownerId.ifBlank { authService.currentUserId ?: "guest" }
            val finalTeam = team.copy(teamId = teamId, ownerId = currentOwner)
            val teamEntity = finalTeam.toEntity()
            teamDao.insertTeam(teamEntity)
            try { kotlinx.coroutines.withTimeoutOrNull(2000) { firestoreService.saveTeam(finalTeam) } } catch (e: Exception) {}
            Result.success(teamId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTeam(team: Team): Result<Unit> {
        return try {
            teamDao.updateTeam(team.toEntity())
            try { kotlinx.coroutines.withTimeoutOrNull(2000) { firestoreService.saveTeam(team) } } catch (e: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTeam(teamId: String): Result<Unit> {
        return try {
            teamDao.deleteTeam(teamId)
            playerDao.deletePlayersForTeam(teamId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPlayer(teamId: String, player: Player): Result<Unit> {
        return try {
            val playerId = if (player.playerId.isNotBlank()) player.playerId else "player_${System.currentTimeMillis()}"
            playerDao.insertPlayer(player.copy(playerId = playerId, teamId = teamId).toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlayer(teamId: String, player: Player): Result<Unit> {
        return try {
            playerDao.updatePlayer(player.copy(teamId = teamId).toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removePlayer(teamId: String, playerId: String): Result<Unit> {
        return try {
            val teamEntity = teamDao.getTeamByIdDirect(teamId)
            if (teamEntity != null) {
                var updatedCaptainId = teamEntity.captainId
                var updatedViceCaptainId = teamEntity.viceCaptainId
                if (teamEntity.captainId == playerId) updatedCaptainId = null
                if (teamEntity.viceCaptainId == playerId) updatedViceCaptainId = null

                if (updatedCaptainId != teamEntity.captainId || updatedViceCaptainId != teamEntity.viceCaptainId) {
                    teamDao.updateTeam(
                        teamEntity.copy(
                            captainId = updatedCaptainId,
                            viceCaptainId = updatedViceCaptainId,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }
            playerDao.deletePlayer(playerId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun assignCaptain(teamId: String, playerId: String): Result<Unit> {
        return try {
            val teamEntity = teamDao.getTeamByIdDirect(teamId) ?: return Result.failure(Exception("Team not found"))

            // Rule 3: If target player is VC, remove VC role first
            var newVcId = teamEntity.viceCaptainId
            if (newVcId == playerId) {
                newVcId = null
            }

            val updatedTeam = teamEntity.copy(
                captainId = playerId,
                viceCaptainId = newVcId,
                updatedAt = System.currentTimeMillis()
            )
            teamDao.updateTeam(updatedTeam)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun assignViceCaptain(teamId: String, playerId: String): Result<Unit> {
        return try {
            val teamEntity = teamDao.getTeamByIdDirect(teamId) ?: return Result.failure(Exception("Team not found"))

            // Rule 3: If target player is Captain, remove Captain role first
            var newCaptainId = teamEntity.captainId
            if (newCaptainId == playerId) {
                newCaptainId = null
            }

            val updatedTeam = teamEntity.copy(
                captainId = newCaptainId,
                viceCaptainId = playerId,
                updatedAt = System.currentTimeMillis()
            )
            teamDao.updateTeam(updatedTeam)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun TeamEntity.toDomain(players: List<Player>): Team {
        return Team(
            teamId = teamId,
            teamName = teamName,
            teamLogo = teamLogo,
            ownerId = ownerId,
            captainId = captainId,
            viceCaptainId = viceCaptainId,
            players = players.map { p ->
                p.copy(
                    isCaptain = p.playerId == captainId,
                    isViceCaptain = p.playerId == viceCaptainId
                )
            },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Team.toEntity(): TeamEntity {
        return TeamEntity(
            teamId = teamId,
            teamName = teamName,
            teamLogo = teamLogo,
            ownerId = ownerId,
            captainId = captainId,
            viceCaptainId = viceCaptainId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun PlayerEntity.toDomain(): Player {
        return Player(
            playerId = playerId,
            teamId = teamId,
            name = name,
            profilePhoto = profilePhoto,
            role = try { PlayerRole.valueOf(role) } catch (e: Exception) { PlayerRole.ALL_ROUNDER },
            battingStyle = try { BattingStyle.valueOf(battingStyle) } catch (e: Exception) { BattingStyle.RIGHT_HAND },
            bowlingStyle = try { BowlingStyle.valueOf(bowlingStyle) } catch (e: Exception) { BowlingStyle.RIGHT_ARM_MEDIUM },
            isCaptain = isCaptain,
            isViceCaptain = isViceCaptain
        )
    }

    private fun Player.toEntity(): PlayerEntity {
        return PlayerEntity(
            playerId = playerId,
            teamId = teamId,
            name = name,
            profilePhoto = profilePhoto,
            role = role.name,
            battingStyle = battingStyle.name,
            bowlingStyle = bowlingStyle.name,
            isCaptain = isCaptain,
            isViceCaptain = isViceCaptain,
            matches = stats.matches,
            runs = stats.runs,
            wickets = stats.wickets,
            ballsFaced = stats.ballsFaced,
            highestScore = stats.highestScore,
            oversBowled = stats.oversBowled,
            runsConceded = stats.runsConceded
        )
    }
}
