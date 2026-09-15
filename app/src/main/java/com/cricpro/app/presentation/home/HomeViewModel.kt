package com.cricpro.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.model.Tournament
import com.cricpro.app.domain.repository.MatchRepository
import com.cricpro.app.domain.repository.TeamRepository
import com.cricpro.app.domain.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val recentMatches: List<Match> = emptyList(),
    val upcomingMatches: List<Match> = emptyList(),
    val completedMatches: List<Match> = emptyList(),
    val teams: List<Team> = emptyList(),
    val tournaments: List<Tournament> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        matchRepository.getRecentMatches(),
        matchRepository.getUpcomingMatches(),
        matchRepository.getCompletedMatches(),
        teamRepository.getTeams(),
        tournamentRepository.getTournaments()
    ) { recents, upcomings, completed, teamsList, toursList ->
        HomeUiState(
            recentMatches = recents.filter { it.status != com.cricpro.app.domain.model.MatchStatus.COMPLETED && it.secondInnings?.isCompleted != true },
            upcomingMatches = upcomings,
            completedMatches = (completed + recents.filter { it.status == com.cricpro.app.domain.model.MatchStatus.COMPLETED || it.secondInnings?.isCompleted == true })
                .distinctBy { it.matchId }
                .sortedByDescending { it.matchDate },
            teams = teamsList,
            tournaments = toursList,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(isLoading = true))
}
