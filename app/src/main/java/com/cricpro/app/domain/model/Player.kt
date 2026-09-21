package com.cricpro.app.domain.model

import com.google.firebase.firestore.Exclude

enum class PlayerRole {
    BATSMAN,
    BOWLER,
    ALL_ROUNDER,
    WICKET_KEEPER
}

enum class BattingStyle {
    RIGHT_HAND,
    LEFT_HAND
}

enum class BowlingStyle {
    RIGHT_ARM_FAST,
    RIGHT_ARM_MEDIUM,
    RIGHT_ARM_SPIN,
    LEFT_ARM_FAST,
    LEFT_ARM_MEDIUM,
    LEFT_ARM_SPIN
}

data class PlayerStats(
    val matches: Int = 0,
    val innings: Int = 0,
    val runs: Int = 0,
    val ballsFaced: Int = 0,
    val highestScore: Int = 0,
    val notOuts: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val wickets: Int = 0,
    val oversBowled: Double = 0.0,
    val runsConceded: Int = 0,
    val maidens: Int = 0,
    val bestBowlingRuns: Int = 0,
    val bestBowlingWickets: Int = 0
) {
    @get:Exclude
    val battingAverage: Double
        get() = if (innings - notOuts > 0) runs.toDouble() / (innings - notOuts) else runs.toDouble()

    @get:Exclude
    val battingStrikeRate: Double
        get() = if (ballsFaced > 0) (runs.toDouble() / ballsFaced) * 100 else 0.0

    @get:Exclude
    val bowlingAverage: Double
        get() = if (wickets > 0) runsConceded.toDouble() / wickets else 0.0

    @get:Exclude
    val economyRate: Double
        get() {
            val totalLegalBalls = (oversBowled.toInt() * 6) + ((oversBowled * 10) % 10).toInt()
            return if (totalLegalBalls > 0) (runsConceded.toDouble() / totalLegalBalls) * 6 else 0.0
        }
}

data class Player(
    val playerId: String = "",
    val teamId: String = "",
    val name: String = "",
    val profilePhoto: String = "",
    val role: PlayerRole = PlayerRole.ALL_ROUNDER,
    val battingStyle: BattingStyle = BattingStyle.RIGHT_HAND,
    val bowlingStyle: BowlingStyle = BowlingStyle.RIGHT_ARM_MEDIUM,
    val isCaptain: Boolean = false,
    val isViceCaptain: Boolean = false,
    val stats: PlayerStats = PlayerStats(),
    val awards: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
