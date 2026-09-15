package com.cricpro.app.presentation.tournament

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.Tournament
import com.cricpro.app.domain.model.TournamentType
import com.cricpro.app.domain.repository.TournamentRepository
import com.cricpro.app.domain.usecase.CalculatePointsTableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository,
    val calculatePointsTableUseCase: CalculatePointsTableUseCase
) : ViewModel() {

    val tournaments: StateFlow<List<Tournament>> = tournamentRepository.getTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createTournament(name: String, type: TournamentType) {
        if (name.isBlank()) return
        viewModelScope.launch {
            tournamentRepository.createTournament(Tournament(name = name, type = type))
        }
    }
}
