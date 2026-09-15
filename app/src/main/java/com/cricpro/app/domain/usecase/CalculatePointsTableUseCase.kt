package com.cricpro.app.domain.usecase

import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.model.PointsTableEntry
import javax.inject.Inject

class CalculatePointsTableUseCase @Inject constructor() {
    operator fun invoke(teamIds: List<String>, teamNamesMap: Map<String, String>, matches: List<Match>): List<PointsTableEntry> {
        val statsMap = teamIds.associateWith { teamId ->
            var played = 0
            var won = 0
            var lost = 0
            var tied = 0
            var noResult = 0
            var runsScored = 0
            var oversFaced = 0.0
            var runsConceded = 0
            var oversBowled = 0.0

            for (match in matches) {
                if (match.status != com.cricpro.app.domain.model.MatchStatus.COMPLETED) continue
                if (match.teamA.teamId != teamId && match.teamB.teamId != teamId) continue

                played++
                val isTeamA = match.teamA.teamId == teamId
                val teamInnings = if (isTeamA) match.firstInnings else match.secondInnings
                val oppInnings = if (isTeamA) match.secondInnings else match.firstInnings

                if (teamInnings != null) {
                    runsScored += teamInnings.totalRuns
                    oversFaced += (teamInnings.legalBallsBowled / 6) + (teamInnings.legalBallsBowled % 6) / 10.0
                }
                if (oppInnings != null) {
                    runsConceded += oppInnings.totalRuns
                    oversBowled += (oppInnings.legalBallsBowled / 6) + (oppInnings.legalBallsBowled % 6) / 10.0
                }

                if (match.winnerTeamId == teamId) {
                    won++
                } else if (match.winnerTeamId != null) {
                    lost++
                } else if (match.resultMessage.contains("Tied", ignoreCase = true)) {
                    tied++
                } else {
                    noResult++
                }
            }

            val points = (won * 2) + (tied * 1) + (noResult * 1)
            val forRate = if (oversFaced > 0) runsScored.toDouble() / oversFaced else 0.0
            val againstRate = if (oversBowled > 0) runsConceded.toDouble() / oversBowled else 0.0
            val nrr = forRate - againstRate

            PointsTableEntry(
                teamId = teamId,
                teamName = teamNamesMap[teamId] ?: "Team $teamId",
                played = played,
                won = won,
                lost = lost,
                tied = tied,
                noResult = noResult,
                points = points,
                netRunRate = String.format("%.3f", nrr).toDoubleOrNull() ?: 0.0,
                runsScoredTotal = runsScored,
                oversFacedTotal = oversFaced,
                runsConcededTotal = runsConceded,
                oversBowledTotal = oversBowled
            )
        }

        return statsMap.values.sortedWith(
            compareByDescending<PointsTableEntry> { it.points }
                .thenByDescending { it.netRunRate }
                .thenByDescending { it.won }
        )
    }
}
