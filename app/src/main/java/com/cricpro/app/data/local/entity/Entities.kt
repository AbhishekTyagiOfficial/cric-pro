package com.cricpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.PlayerRole
import com.cricpro.app.domain.model.BattingStyle
import com.cricpro.app.domain.model.BowlingStyle
import com.cricpro.app.domain.model.Team

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val teamId: String = "",
    val teamName: String = "",
    val teamLogo: String = "",
    val ownerId: String = "",
    val captainId: String? = null,
    val viceCaptainId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

fun Team.toTeamEntity(): TeamEntity {
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

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val playerId: String = "",
    val teamId: String = "",
    val name: String = "",
    val profilePhoto: String = "",
    val role: String = "ALL_ROUNDER",
    val battingStyle: String = "RIGHT_HAND",
    val bowlingStyle: String = "RIGHT_ARM_MEDIUM",
    val isCaptain: Boolean = false,
    val isViceCaptain: Boolean = false,
    val matches: Int = 0,
    val runs: Int = 0,
    val wickets: Int = 0,
    val ballsFaced: Int = 0,
    val highestScore: Int = 0,
    val oversBowled: Double = 0.0,
    val runsConceded: Int = 0
)

fun Player.toPlayerEntity(): PlayerEntity {
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

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val matchId: String,
    val tournamentId: String?,
    val title: String,
    val matchType: String,
    val totalOvers: Int,
    val groundName: String,
    val matchDate: Long,
    val teamAId: String,
    val teamAName: String,
    val teamBId: String,
    val teamBName: String,
    val tossWinnerId: String?,
    val tossDecision: String?,
    val status: String,
    val currentInningsNumber: Int,
    val resultMessage: String,
    val winnerTeamId: String?,
    val creatorId: String,
    val scorerId: String,
    val matchJson: String // Serialized full match state for offline resilience
)

@Entity(tableName = "balls")
data class BallEntity(
    @PrimaryKey val ballId: String,
    val matchId: String,
    val inningsNumber: Int,
    val overNumber: Int,
    val ballNumberInOver: Int,
    val strikerId: String,
    val nonStrikerId: String,
    val bowlerId: String,
    val runsScored: Int,
    val extraType: String,
    val extraRuns: Int,
    val isLegalDelivery: Boolean,
    val wicketType: String,
    val dismissedPlayerId: String?,
    val timestamp: Long
)

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val tournamentId: String,
    val name: String,
    val logoUrl: String,
    val type: String,
    val organizerId: String,
    val startDate: Long,
    val endDate: Long,
    val tournamentJson: String
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val entityId: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
