package com.cricpro.app.domain.model

enum class TournamentType {
    LEAGUE,
    KNOCKOUT,
    LEAGUE_AND_KNOCKOUT
}

data class Fixture(
    val fixtureId: String = "",
    val tournamentId: String = "",
    val teamAId: String = "",
    val teamAName: String = "",
    val teamBId: String = "",
    val teamBName: String = "",
    val date: Long = System.currentTimeMillis(),
    val matchId: String? = null,
    val status: String = "UPCOMING"
)

data class PointsTableEntry(
    val teamId: String = "",
    val teamName: String = "",
    val played: Int = 0,
    val won: Int = 0,
    val lost: Int = 0,
    val tied: Int = 0,
    val noResult: Int = 0,
    val points: Int = 0,
    val netRunRate: Double = 0.0,
    val runsScoredTotal: Int = 0,
    val oversFacedTotal: Double = 0.0,
    val runsConcededTotal: Int = 0,
    val oversBowledTotal: Double = 0.0
)

data class Tournament(
    val tournamentId: String = "",
    val name: String = "",
    val logoUrl: String = "",
    val type: TournamentType = TournamentType.LEAGUE,
    val organizerId: String = "",
    val teamIds: List<String> = emptyList(),
    val fixtures: List<Fixture> = emptyList(),
    val pointsTable: List<PointsTableEntry> = emptyList(),
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long = System.currentTimeMillis()
)
