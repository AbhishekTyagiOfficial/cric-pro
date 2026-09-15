package com.cricpro.app.presentation.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.model.MatchType
import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val matchRepository: MatchRepository
) : ViewModel() {

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    fun createMatch(
        title: String,
        groundName: String,
        overs: Int,
        teamA: Team,
        teamB: Team,
        onMatchCreated: (String) -> Unit
    ) {
        if (_isCreating.value) return
        _isCreating.value = true

        val matchId = "match_${System.currentTimeMillis()}"
        val match = Match(
            matchId = matchId,
            title = title.ifEmpty { "${teamA.teamName} vs ${teamB.teamName}" },
            matchType = if (overs <= 10) MatchType.T10 else if (overs <= 20) MatchType.T20 else MatchType.ODI,
            totalOvers = overs,
            groundName = groundName.ifEmpty { "Main Ground" },
            teamA = teamA,
            teamB = teamB
        )

        viewModelScope.launch {
            try {
                matchRepository.createMatch(match)
                onMatchCreated(matchId)
            } finally {
                _isCreating.value = false
            }
        }
    }
}
