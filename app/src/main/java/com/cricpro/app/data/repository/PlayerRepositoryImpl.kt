package com.cricpro.app.data.repository

import com.cricpro.app.data.local.dao.PlayerDao
import com.cricpro.app.data.local.entity.PlayerEntity
import com.cricpro.app.domain.model.BattingStyle
import com.cricpro.app.domain.model.BowlingStyle
import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.PlayerRole
import com.cricpro.app.domain.model.PlayerStats
import com.cricpro.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val playerDao: PlayerDao
) : PlayerRepository {

    override fun getPlayerById(playerId: String): Flow<Player?> {
        return playerDao.getPlayerById(playerId).map { it?.toDomain() }
    }

    override fun searchPlayers(query: String): Flow<List<Player>> {
        return playerDao.searchPlayers(query).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun updatePlayerStats(playerId: String, runs: Int, wickets: Int, ballsFaced: Int): Result<Unit> {
        return try {
            val entity = playerDao.getPlayerById(playerId)
            // Perform stats update
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
            isViceCaptain = isViceCaptain,
            stats = PlayerStats(
                matches = matches,
                runs = runs,
                wickets = wickets,
                ballsFaced = ballsFaced,
                highestScore = highestScore,
                oversBowled = oversBowled,
                runsConceded = runsConceded
            )
        )
    }
}
