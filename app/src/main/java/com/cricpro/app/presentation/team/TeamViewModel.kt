package com.cricpro.app.presentation.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.repository.TeamRepository
import com.cricpro.app.domain.usecase.AssignCaptainUseCase
import com.cricpro.app.domain.usecase.AssignViceCaptainUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository,
    private val assignCaptainUseCase: AssignCaptainUseCase,
    private val assignViceCaptainUseCase: AssignViceCaptainUseCase
) : ViewModel() {

    val teams: StateFlow<List<Team>> = teamRepository.getTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createTeam(teamName: String) {
        if (teamName.isBlank()) return
        viewModelScope.launch {
            teamRepository.createTeam(Team(teamName = teamName))
        }
    }

    fun addPlayerToTeam(teamId: String, player: Player) {
        viewModelScope.launch {
            teamRepository.addPlayer(teamId, player)
        }
    }

    fun removePlayerFromTeam(teamId: String, playerId: String) {
        viewModelScope.launch {
            teamRepository.removePlayer(teamId, playerId)
        }
    }

    fun assignCaptain(teamId: String, playerId: String) {
        viewModelScope.launch {
            assignCaptainUseCase(teamId, playerId)
        }
    }

    fun assignViceCaptain(teamId: String, playerId: String) {
        viewModelScope.launch {
            assignViceCaptainUseCase(teamId, playerId)
        }
    }

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId)
        }
    }
}
