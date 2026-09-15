package com.cricpro.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val teamId: String,
    val teamName: String,
    val teamLogo: String,
    val ownerId: String,
    val captainId: String?,
    val viceCaptainId: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val playerId: String,
    val teamId: String,
    val name: String,
    val profilePhoto: String,
    val role: String,
    val battingStyle: String,
    val bowlingStyle: String,
    val isCaptain: Boolean,
    val isViceCaptain: Boolean,
    val matches: Int,
    val runs: Int,
    val wickets: Int,
    val ballsFaced: Int,
    val highestScore: Int,
    val oversBowled: Double,
    val runsConceded: Int
)

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
