package com.cricpro.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.model.Tournament
import com.cricpro.app.domain.repository.MatchRepository
import com.cricpro.app.domain.repository.TeamRepository
import com.cricpro.app.domain.repository.TournamentRepository
import com.cricpro.app.data.remote.RemoteConfigManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentMatches: List<Match> = emptyList(),
    val upcomingMatches: List<Match> = emptyList(),
    val completedMatches: List<Match> = emptyList(),
    val teams: List<Team> = emptyList(),
    val tournaments: List<Tournament> = emptyList(),
    val isTournamentTabEnabled: Boolean = true,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
    private val tournamentRepository: TournamentRepository,
    tournamentTabFlow: Flow<Boolean>,
    private val customScope: CoroutineScope? = null
) : ViewModel() {

    @Inject
    constructor(
        matchRepository: MatchRepository,
        teamRepository: TeamRepository,
        tournamentRepository: TournamentRepository,
        remoteConfigManager: RemoteConfigManager
    ) : this(
        matchRepository,
        teamRepository,
        tournamentRepository,
        remoteConfigManager.isTournamentTabEnabled,
        null
    )

    constructor(
        matchRepository: MatchRepository,
        teamRepository: TeamRepository,
        tournamentRepository: TournamentRepository,
        testScope: CoroutineScope
    ) : this(
        matchRepository,
        teamRepository,
        tournamentRepository,
        flowOf(true),
        testScope
    )

    private val activeScope: CoroutineScope get() = customScope ?: viewModelScope

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        matchRepository.getRecentMatches(),
        matchRepository.getUpcomingMatches(),
        matchRepository.getCompletedMatches(),
        combine(
            teamRepository.getTeams(),
            tournamentRepository.getTournaments(),
            tournamentTabFlow
        ) { teamsList, toursList, tournamentTabEnabled ->
            Triple(teamsList, toursList, tournamentTabEnabled)
        }
    ) { recents, upcomings, completed, (teamsList, toursList, tournamentTabEnabled) ->
        HomeUiState(
            recentMatches = recents.filter { it.status != com.cricpro.app.domain.model.MatchStatus.COMPLETED && it.secondInnings?.isCompleted != true },
            upcomingMatches = upcomings,
            completedMatches = (completed + recents.filter { it.status == com.cricpro.app.domain.model.MatchStatus.COMPLETED || it.secondInnings?.isCompleted == true })
                .distinctBy { it.matchId }
                .sortedByDescending { it.matchDate },
            teams = teamsList,
            tournaments = toursList,
            isTournamentTabEnabled = tournamentTabEnabled,
            isLoading = false
        )
    }.stateIn(activeScope, SharingStarted.WhileSubscribed(5000), HomeUiState(isLoading = true))

    fun deleteMatch(
        matchId: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (_isDeleting.value) return
        _isDeleting.value = true
        activeScope.launch {
            try {
                val result = matchRepository.deleteMatch(matchId)
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    onError(result.exceptionOrNull()?.localizedMessage ?: "Failed to delete match")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Failed to delete match")
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
