package com.cricpro.app.domain.model

data class BatterScore(
    val playerId: String = "",
    val name: String = "",
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val isOut: Boolean = false,
    val dismissalInfo: String = "not out"
) {
    val strikeRate: Double
        get() = if (balls > 0) (runs.toDouble() / balls) * 100 else 0.0
}

data class BowlerScore(
    val playerId: String = "",
    val name: String = "",
    val legalBalls: Int = 0,
    val maidens: Int = 0,
    val runsConceded: Int = 0,
    val wickets: Int = 0,
    val wides: Int = 0,
    val noBalls: Int = 0
) {
    val oversFormatted: String
        get() = "${legalBalls / 6}.${legalBalls % 6}"

    val economy: Double
        get() = if (legalBalls > 0) (runsConceded.toDouble() / legalBalls) * 6 else 0.0
}

data class FallOfWicket(
    val wicketNumber: Int = 0,
    val score: Int = 0,
    val oversFormatted: String = "0.0",
    val dismissedPlayerName: String = ""
)

data class Partnership(
    val player1Id: String = "",
    val player2Id: String = "",
    val runs: Int = 0,
    val balls: Int = 0
)

data class ExtrasSummary(
    val wides: Int = 0,
    val noBalls: Int = 0,
    val byes: Int = 0,
    val legByes: Int = 0
) {
    val total: Int
        get() = wides + noBalls + byes + legByes
}

data class Innings(
    val inningsNumber: Int = 1,
    val battingTeamId: String = "",
    val bowlingTeamId: String = "",
    val totalRuns: Int = 0,
    val wickets: Int = 0,
    val legalBallsBowled: Int = 0,
    val target: Int? = null,
    val isCompleted: Boolean = false,
    val batters: Map<String, BatterScore> = emptyMap(),
    val bowlers: Map<String, BowlerScore> = emptyMap(),
    val fallOfWickets: List<FallOfWicket> = emptyList(),
    val partnerships: List<Partnership> = emptyList(),
    val extras: ExtrasSummary = ExtrasSummary(),
    val ballsHistory: List<Ball> = emptyList()
) {
    val oversFormatted: String
        get() = "${legalBallsBowled / 6}.${legalBallsBowled % 6}"

    val currentRunRate: Double
        get() = if (legalBallsBowled > 0) (totalRuns.toDouble() / legalBallsBowled) * 6 else 0.0

    val runRateFormatted: String
        get() = String.format(java.util.Locale.US, "%.2f", currentRunRate)
}
