package com.cricpro.app.domain.model

enum class MatchType {
    T20,
    T10,
    ODI,
    TEST,
    CUSTOM
}

enum class MatchStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}

enum class TossDecision {
    BAT,
    BOWL
}

data class Match(
    val matchId: String = "",
    val tournamentId: String? = null,
    val title: String = "",
    val matchType: MatchType = MatchType.T20,
    val totalOvers: Int = 20,
    val groundName: String = "",
    val matchDate: Long = System.currentTimeMillis(),
    val teamA: Team = Team(),
    val teamB: Team = Team(),
    val playingXI_A: List<String> = emptyList(), // playerIds
    val playingXI_B: List<String> = emptyList(),
    val tossWinnerId: String? = null,
    val tossDecision: TossDecision? = null,
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val currentInningsNumber: Int = 1,
    val firstInnings: Innings? = null,
    val secondInnings: Innings? = null,
    val currentStrikerId: String? = null,
    val currentNonStrikerId: String? = null,
    val currentBowlerId: String? = null,
    val resultMessage: String = "",
    val winnerTeamId: String? = null,
    val creatorId: String = "",
    val scorerId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
