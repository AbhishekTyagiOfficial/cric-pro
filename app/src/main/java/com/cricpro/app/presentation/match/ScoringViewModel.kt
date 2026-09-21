package com.cricpro.app.presentation.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.*
import com.cricpro.app.domain.repository.MatchRepository
import com.cricpro.app.domain.repository.ScoringRepository
import com.cricpro.app.domain.repository.TeamRepository
import com.cricpro.app.domain.usecase.EditBallUseCase
import com.cricpro.app.domain.usecase.ScoreBallUseCase
import com.cricpro.app.domain.usecase.UndoBallUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScoringUiState(
    val currentMatch: Match? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedExtraType: ExtraType = ExtraType.NONE,
    val selectedWicketType: WicketType = WicketType.NONE,
    val wagonWheelAngle: Float = 0f,
    val showInningsBreakDialog: Boolean = false,
    val showMatchCompletedDialog: Boolean = false
)

@HiltViewModel
class ScoringViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val teamRepository: TeamRepository,
    private val scoringRepository: ScoringRepository,
    private val scoreBallUseCase: ScoreBallUseCase,
    private val undoBallUseCase: UndoBallUseCase,
    private val editBallUseCase: EditBallUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchId: String = savedStateHandle.get<String>("matchId") ?: ""
    private var hasUserDismissedInningsBreak = false
    private var hasUserDismissedMatchCompleted = false

    private val _overrideBowlerId = MutableStateFlow<String?>(null)
    private val _overrideStrikerId = MutableStateFlow<String?>(null)
    private val _overrideNonStrikerId = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(
        ScoringUiState(
            currentMatch = Match(
                matchId = matchId.ifEmpty { "match_${System.currentTimeMillis()}" },
                title = "Live Cricket Match",
                teamA = Team(teamId = "t1", teamName = "Team Alpha"),
                teamB = Team(teamId = "t2", teamName = "Team Beta"),
                firstInnings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2"),
                secondInnings = Innings(inningsNumber = 2, battingTeamId = "t2", bowlingTeamId = "t1")
            ),
            isLoading = false
        )
    )
    val uiState: StateFlow<ScoringUiState> = _uiState.asStateFlow()

    init {
        if (matchId.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                combine(
                    matchRepository.getMatchById(matchId),
                    teamRepository.getTeams(),
                    scoringRepository.getBallsForMatch(matchId, 1),
                    scoringRepository.getBallsForMatch(matchId, 2)
                ) { rawMatch, teams, balls1, balls2 ->
                    if (rawMatch == null) null
                    else {
                        val tA = teams.find { it.teamId == rawMatch.teamA.teamId || (it.teamName.equals(rawMatch.teamA.teamName, ignoreCase = true) && rawMatch.teamA.teamName.isNotBlank()) } ?: rawMatch.teamA
                        val tB = teams.find { (it.teamId == rawMatch.teamB.teamId || (it.teamName.equals(rawMatch.teamB.teamName, ignoreCase = true) && rawMatch.teamB.teamName.isNotBlank())) && (tA.teamId.isBlank() || it.teamId != tA.teamId) } ?: rawMatch.teamB
                        val fullMatch = rawMatch.copy(teamA = tA, teamB = tB)
                        Triple(fullMatch, balls1, balls2)
                    }
                }.flowOn(Dispatchers.IO).collect { triple ->
                    if (triple == null) return@collect
                    val (match, balls1, balls2) = triple

                    val totalOvers = match.totalOvers
                    val scoringEngine = com.cricpro.app.domain.engine.ScoringEngine()

                    val inn1Base = match.firstInnings ?: Innings(1, match.teamA.teamId, match.teamB.teamId)
                    val inn2Base = match.secondInnings ?: Innings(2, match.teamB.teamId, match.teamA.teamId)

                    fun getMaxWicketsForTeam(team: Team): Int {
                        return if (team.players.size > 1) (team.players.size - 1).coerceAtMost(10) else 10
                    }

                    val batTeam1 = if (inn1Base.battingTeamId == match.teamA.teamId) match.teamA else match.teamB
                    val maxWickets1 = getMaxWicketsForTeam(batTeam1)

                    val res1 = scoringEngine.recalculateInnings(
                        balls = balls1,
                        battingTeamId = inn1Base.battingTeamId,
                        bowlingTeamId = inn1Base.bowlingTeamId,
                        inningsNumber = 1,
                        target = null,
                        totalOversInMatch = totalOvers,
                        maxWickets = maxWickets1
                    )

                    var updatedInn1 = res1.updatedInnings
                    val maxBalls = totalOvers * 6
                    if (updatedInn1.legalBallsBowled >= maxBalls || updatedInn1.wickets >= maxWickets1) {
                        updatedInn1 = updatedInn1.copy(isCompleted = true)
                    }

                    val targetForInn2 = if (updatedInn1.isCompleted || balls1.isNotEmpty()) updatedInn1.totalRuns + 1 else null
                    val updatedInn2Base = inn2Base.copy(target = targetForInn2)

                    val batTeam2 = if (inn2Base.battingTeamId == match.teamA.teamId) match.teamA else match.teamB
                    val maxWickets2 = getMaxWicketsForTeam(batTeam2)

                    val res2 = scoringEngine.recalculateInnings(
                        balls = balls2,
                        battingTeamId = updatedInn2Base.battingTeamId,
                        bowlingTeamId = updatedInn2Base.bowlingTeamId,
                        inningsNumber = 2,
                        target = targetForInn2,
                        totalOversInMatch = totalOvers,
                        maxWickets = maxWickets2
                    )

                    var updatedInn2 = res2.updatedInnings
                    if (updatedInn2.isCompleted || (targetForInn2 != null && updatedInn2.totalRuns >= targetForInn2) || updatedInn2.legalBallsBowled >= maxBalls || updatedInn2.wickets >= maxWickets2) {
                        updatedInn2 = updatedInn2.copy(isCompleted = true)
                    }

                    var activeInningsNum = match.currentInningsNumber
                    var showInningsBreak = false

                    if (updatedInn1.isCompleted) {
                        if (balls2.isEmpty() && !hasUserDismissedInningsBreak) {
                            showInningsBreak = true
                        } else {
                            activeInningsNum = 2
                        }
                    }

                    var mStatus = match.status
                    var resultMsg = match.resultMessage
                    var winnerId = match.winnerTeamId

                    if (updatedInn2.isCompleted) {
                        mStatus = MatchStatus.COMPLETED
                        val batTeamName = if (updatedInn2.battingTeamId == match.teamA.teamId) match.teamA.teamName else match.teamB.teamName
                        val bowlTeamName = if (updatedInn2.bowlingTeamId == match.teamA.teamId) match.teamA.teamName else match.teamB.teamName

                        val inn1Runs = updatedInn1.totalRuns
                        val inn2Runs = updatedInn2.totalRuns
                        val targetVal = targetForInn2 ?: (inn1Runs + 1)

                        if (inn2Runs >= targetVal) {
                            winnerId = updatedInn2.battingTeamId
                            val wktsLeft = maxWickets2 - updatedInn2.wickets
                            resultMsg = "$batTeamName won by $wktsLeft wickets!"
                        } else if (inn1Runs > inn2Runs) {
                            winnerId = updatedInn1.battingTeamId
                            val runsDiff = inn1Runs - inn2Runs
                            resultMsg = "$bowlTeamName won by $runsDiff runs!"
                        } else {
                            resultMsg = "Match Tied!"
                        }
                    }

                    var showMatchCompleted = false
                    if (mStatus == MatchStatus.COMPLETED && !hasUserDismissedMatchCompleted) {
                        showMatchCompleted = true
                    }

                    val activeRes = if (activeInningsNum == 1) res1 else res2

                    fun isTeamA(idOrName: String?, teamA: Team, teamB: Team): Boolean {
                        if (idOrName.isNullOrBlank()) return true
                        if (idOrName == teamB.teamId || (teamB.teamName.isNotBlank() && idOrName.equals(teamB.teamName, ignoreCase = true))) {
                            return false
                        }
                        if (idOrName == teamA.teamId || (teamA.teamName.isNotBlank() && idOrName.equals(teamA.teamName, ignoreCase = true))) {
                            return true
                        }
                        return true
                    }

                    val activeBattingTeamId = if (activeInningsNum == 1) inn1Base.battingTeamId else inn2Base.battingTeamId
                    val isBattingTeamA = isTeamA(activeBattingTeamId, match.teamA, match.teamB)

                    val batTeamActive = if (isBattingTeamA) match.teamA else match.teamB
                    val bowlTeamActive = if (isBattingTeamA) match.teamB else match.teamA

                    val defaultStriker = batTeamActive.players.getOrNull(0)?.name ?: if (batTeamActive.teamName.isNotBlank()) "${batTeamActive.teamName} Player 1" else "Player 1"
                    val defaultNonStriker = batTeamActive.players.getOrNull(1)?.name ?: if (batTeamActive.teamName.isNotBlank()) "${batTeamActive.teamName} Player 2" else "Player 2"
                    val defaultBowler = bowlTeamActive.players.getOrNull(0)?.name ?: if (bowlTeamActive.teamName.isNotBlank()) "${bowlTeamActive.teamName} Bowler 1" else "Bowler 1"

                    if (_overrideBowlerId.value != null && match.currentBowlerId == _overrideBowlerId.value) {
                        _overrideBowlerId.value = null
                    }
                    if (_overrideStrikerId.value != null && match.currentStrikerId == _overrideStrikerId.value) {
                        _overrideStrikerId.value = null
                    }
                    if (_overrideNonStrikerId.value != null && match.currentNonStrikerId == _overrideNonStrikerId.value) {
                        _overrideNonStrikerId.value = null
                    }

                    fun getBatterScore(name: String?): BatterScore? {
                        if (name.isNullOrBlank()) return null
                        return activeRes.updatedInnings.batters[name]
                            ?: activeRes.updatedInnings.batters.values.find { it.name == name || it.playerId == name }
                    }

                    if (_overrideStrikerId.value != null && getBatterScore(_overrideStrikerId.value)?.isOut == true) {
                        _overrideStrikerId.value = null
                    }
                    if (_overrideNonStrikerId.value != null && getBatterScore(_overrideNonStrikerId.value)?.isOut == true) {
                        _overrideNonStrikerId.value = null
                    }

                    fun isPlayerInTeamSquad(playerNameOrId: String?, team: Team, otherTeam: Team): Boolean {
                        if (playerNameOrId.isNullOrBlank()) return false
                        if (team.players.isNotEmpty()) {
                            val inThisTeam = team.players.any {
                                it.name.equals(playerNameOrId, ignoreCase = true) || it.playerId == playerNameOrId
                            }
                            if (inThisTeam) return true
                            if (otherTeam.players.isNotEmpty()) {
                                val inOtherTeam = otherTeam.players.any {
                                    it.name.equals(playerNameOrId, ignoreCase = true) || it.playerId == playerNameOrId
                                }
                                if (inOtherTeam) return false
                            }
                        }
                        if (team.teamName.isNotBlank() && playerNameOrId.contains(team.teamName, ignoreCase = true)) {
                            return true
                        }
                        if (otherTeam.teamName.isNotBlank() && playerNameOrId.contains(otherTeam.teamName, ignoreCase = true)) {
                            return false
                        }
                        return true
                    }

                    val isSecondInningsNoBalls = (activeInningsNum == 2 && balls2.isEmpty())

                    val engineStriker = activeRes.nextStrikerId
                    val engineNonStriker = activeRes.nextNonStrikerId

                    fun isBatterNotOut(name: String?): Boolean {
                        if (name.isNullOrBlank()) return false
                        val bScore = getBatterScore(name)
                        if (bScore != null) return !bScore.isOut
                        return isPlayerInTeamSquad(name, batTeamActive, bowlTeamActive)
                    }

                    val rawStriker = _overrideStrikerId.value
                        ?: if (isBatterNotOut(engineStriker)) engineStriker else null
                        ?: if (isBatterNotOut(match.currentStrikerId)) match.currentStrikerId else null
                        ?: engineStriker ?: match.currentStrikerId

                    val rawNonStriker = _overrideNonStrikerId.value
                        ?: if (isBatterNotOut(engineNonStriker)) engineNonStriker else null
                        ?: if (isBatterNotOut(match.currentNonStrikerId)) match.currentNonStrikerId else null
                        ?: engineNonStriker ?: match.currentNonStrikerId

                    val activeBowlerFromStream = _overrideBowlerId.value ?: match.currentBowlerId

                    val squadNames = if (batTeamActive.players.isNotEmpty()) {
                        batTeamActive.players.map { it.name }
                    } else {
                        (1..11).map { if (batTeamActive.teamName.isNotBlank()) "${batTeamActive.teamName} Player $it" else "Player $it" }
                    }
                    val existingBatterNames = activeRes.updatedInnings.batters.values.map { if (it.name.isNotBlank()) it.name else it.playerId }
                    val allBattingCandidates = (squadNames + existingBatterNames).distinct().filter { it.isNotBlank() }

                    val maxWicketsActive = getMaxWicketsForTeam(batTeamActive)

                    fun findNextIncomingBatter(otherBatterName: String?): String? {
                        if (activeRes.updatedInnings.wickets >= maxWicketsActive) return null
                        val found = allBattingCandidates.find { name ->
                            val bScore = getBatterScore(name)
                            val isOut = bScore?.isOut == true
                            val isOther = (name == otherBatterName)
                            !isOut && !isOther
                        }
                        if (found != null) return found
                        if (batTeamActive.players.isNotEmpty()) return null
                        if (activeRes.updatedInnings.wickets >= 10) return null
                        return "Player ${(activeRes.updatedInnings.wickets + 2)}"
                    }

                    val strikerIsOut = getBatterScore(rawStriker)?.isOut == true
                    val nonStrikerIsOut = getBatterScore(rawNonStriker)?.isOut == true

                    val resolvedStriker = if (strikerIsOut) {
                        _overrideStrikerId.value ?: findNextIncomingBatter(rawNonStriker) ?: rawStriker
                    } else {
                        rawStriker
                    }

                    val resolvedNonStriker = if (nonStrikerIsOut) {
                        _overrideNonStrikerId.value ?: findNextIncomingBatter(resolvedStriker) ?: rawNonStriker
                    } else {
                        rawNonStriker
                    }

                    val isStrikerInvalidForTeam = !isPlayerInTeamSquad(match.currentStrikerId, batTeamActive, bowlTeamActive)
                    val isNonStrikerInvalidForTeam = !isPlayerInTeamSquad(match.currentNonStrikerId, batTeamActive, bowlTeamActive)
                    val isBowlerInvalidForTeam = !isPlayerInTeamSquad(match.currentBowlerId, bowlTeamActive, batTeamActive)

                    val defaultStrikerToUse = if ((isSecondInningsNoBalls || isStrikerInvalidForTeam) && _overrideStrikerId.value == null) defaultStriker else null
                    val defaultNonStrikerToUse = if ((isSecondInningsNoBalls || isNonStrikerInvalidForTeam) && _overrideNonStrikerId.value == null) defaultNonStriker else null
                    val defaultBowlerToUse = if ((isSecondInningsNoBalls || isBowlerInvalidForTeam) && _overrideBowlerId.value == null) defaultBowler else null

                    val currentStriker = defaultStrikerToUse ?: resolvedStriker ?: defaultStriker

                    var currentNonStriker = defaultNonStrikerToUse ?: resolvedNonStriker ?: defaultNonStriker
                    if (currentNonStriker == currentStriker) {
                        val altNonStriker = squadNames.find { it != currentStriker && getBatterScore(it)?.isOut != true }
                            ?: findNextIncomingBatter(currentStriker)
                            ?: if (batTeamActive.teamName.isNotBlank()) "${batTeamActive.teamName} Player 2" else "Player 2"
                        currentNonStriker = altNonStriker
                    }

                    var currentBowler = defaultBowlerToUse ?: (if (!isBowlerInvalidForTeam) activeBowlerFromStream else null) ?: defaultBowler
                    if (currentBowler == currentStriker || currentBowler == currentNonStriker) {
                        _overrideBowlerId.value = null
                        val bowlSquad = if (bowlTeamActive.players.isNotEmpty()) {
                            bowlTeamActive.players.map { it.name }
                        } else {
                            (1..11).map { if (bowlTeamActive.teamName.isNotBlank()) "${bowlTeamActive.teamName} Bowler $it" else "Bowler $it" }
                        }
                        val validBowler = bowlSquad.find { it != currentStriker && it != currentNonStriker }
                            ?: if (bowlTeamActive.teamName.isNotBlank()) "${bowlTeamActive.teamName} Bowler 1" else "Bowler 1"
                        currentBowler = validBowler
                    }

                    val isMatchJustCompleted = (mStatus == MatchStatus.COMPLETED)
                    val finalMatchDate = if (isMatchJustCompleted && match.status != MatchStatus.COMPLETED) System.currentTimeMillis() else match.matchDate

                    val updatedMatch = match.copy(
                        firstInnings = updatedInn1,
                        secondInnings = updatedInn2,
                        currentInningsNumber = activeInningsNum,
                        currentStrikerId = currentStriker,
                        currentNonStrikerId = currentNonStriker,
                        currentBowlerId = currentBowler,
                        status = mStatus,
                        resultMessage = resultMsg,
                        winnerTeamId = winnerId,
                        matchDate = finalMatchDate,
                        updatedAt = System.currentTimeMillis()
                    )
                    _uiState.value = _uiState.value.copy(
                        currentMatch = updatedMatch,
                        isLoading = false,
                        showInningsBreakDialog = showInningsBreak,
                        showMatchCompletedDialog = showMatchCompleted
                    )

                    if (updatedMatch.status != match.status ||
                        updatedMatch.firstInnings != match.firstInnings ||
                        updatedMatch.secondInnings != match.secondInnings ||
                        updatedMatch.currentStrikerId != match.currentStrikerId ||
                        updatedMatch.currentNonStrikerId != match.currentNonStrikerId ||
                        updatedMatch.currentBowlerId != match.currentBowlerId ||
                        updatedMatch.status == MatchStatus.COMPLETED
                    ) {
                        viewModelScope.launch {
                            scoringRepository.syncInningsState(updatedMatch.matchId, updatedMatch)
                        }
                    }
                }
            }
        }
    }

    fun scoreRuns(runs: Int, extraType: ExtraType = ExtraType.NONE, wicketType: WicketType = WicketType.NONE) {
        _overrideStrikerId.value = null
        _overrideNonStrikerId.value = null
        _overrideBowlerId.value = null

        val baseMatch = uiState.value.currentMatch ?: Match(
            matchId = matchId.ifEmpty { "match_${System.currentTimeMillis()}" },
            teamA = Team(teamId = "t1", teamName = "Team Alpha"),
            teamB = Team(teamId = "t2", teamName = "Team Beta"),
            firstInnings = Innings(inningsNumber = 1, battingTeamId = "t1", bowlingTeamId = "t2"),
            secondInnings = Innings(inningsNumber = 2, battingTeamId = "t2", bowlingTeamId = "t1")
        )

        if (baseMatch.status == MatchStatus.COMPLETED) {
            _uiState.value = _uiState.value.copy(errorMessage = "Match is already completed!")
            return
        }

        val inn1 = baseMatch.firstInnings ?: Innings(1, baseMatch.teamA.teamId, baseMatch.teamB.teamId)
        val inn2 = baseMatch.secondInnings ?: Innings(2, baseMatch.teamB.teamId, baseMatch.teamA.teamId)
        val match = baseMatch.copy(firstInnings = inn1, secondInnings = inn2)

        val isLegal = extraType != ExtraType.WIDE && extraType != ExtraType.NO_BALL
        val extraRunsVal = when (extraType) {
            ExtraType.WIDE, ExtraType.NO_BALL -> 1
            else -> 0
        }

        val ball = Ball(
            matchId = match.matchId,
            inningsNumber = match.currentInningsNumber,
            strikerId = match.currentStrikerId ?: "P1",
            nonStrikerId = match.currentNonStrikerId ?: "P2",
            bowlerId = match.currentBowlerId ?: "B1",
            runsScored = runs,
            extraType = extraType,
            extraRuns = extraRunsVal,
            isLegalDelivery = isLegal,
            wicketType = wicketType,
            wagonWheel = WagonWheelPoint(angleDegrees = uiState.value.wagonWheelAngle, distanceFraction = 0.8f)
        )

        viewModelScope.launch {
            val result = scoreBallUseCase(match, ball)
            result.fold(
                onSuccess = { scoringResult ->
                    _uiState.value = _uiState.value.copy(isLoading = false, wagonWheelAngle = 0f)
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
                }
            )
        }
    }

    fun undoLastBall() {
        val match = uiState.value.currentMatch ?: return
        viewModelScope.launch {
            val result = undoBallUseCase(match)
            result.fold(
                onSuccess = { updatedMatch ->
                    _uiState.value = _uiState.value.copy(currentMatch = updatedMatch)
                },
                onFailure = {}
            )
        }
    }

    fun setWagonWheelAngle(angle: Float) {
        _uiState.value = _uiState.value.copy(wagonWheelAngle = angle)
    }

    fun selectToss(tossWinnerId: String, decision: TossDecision) {
        if (matchId.isBlank()) return
        viewModelScope.launch {
            matchRepository.updateMatchToss(matchId, tossWinnerId, decision)
        }
    }

    fun selectStriker(name: String) {
        val match = uiState.value.currentMatch ?: return
        if (name == match.currentNonStrikerId || name == match.currentBowlerId) return
        _overrideStrikerId.value = name
        val updated = match.copy(currentStrikerId = name, updatedAt = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(currentMatch = updated)
        viewModelScope.launch { scoringRepository.syncInningsState(match.matchId, updated) }
    }

    fun selectNonStriker(name: String) {
        val match = uiState.value.currentMatch ?: return
        if (name == match.currentStrikerId || name == match.currentBowlerId) return
        _overrideNonStrikerId.value = name
        val updated = match.copy(currentNonStrikerId = name, updatedAt = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(currentMatch = updated)
        viewModelScope.launch { scoringRepository.syncInningsState(match.matchId, updated) }
    }

    fun selectBowler(name: String) {
        val match = uiState.value.currentMatch ?: return
        if (name == match.currentStrikerId || name == match.currentNonStrikerId) return
        _overrideBowlerId.value = name
        val updated = match.copy(currentBowlerId = name, updatedAt = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(currentMatch = updated)
        viewModelScope.launch { scoringRepository.syncInningsState(match.matchId, updated) }
    }

    fun swapStrike() {
        val match = uiState.value.currentMatch ?: return
        val newStriker = match.currentNonStrikerId ?: "Batter 2"
        val newNonStriker = match.currentStrikerId ?: "Batter 1"
        _overrideStrikerId.value = newStriker
        _overrideNonStrikerId.value = newNonStriker
        val updated = match.copy(
            currentStrikerId = newStriker,
            currentNonStrikerId = newNonStriker,
            updatedAt = System.currentTimeMillis()
        )
        _uiState.value = _uiState.value.copy(currentMatch = updated)
        viewModelScope.launch { scoringRepository.syncInningsState(match.matchId, updated) }
    }

    fun dismissInningsBreakDialog() {
        hasUserDismissedInningsBreak = true
        val currentM = _uiState.value.currentMatch
        if (currentM != null) {
            val inn2 = currentM.secondInnings
            val batTeam2 = if (inn2?.battingTeamId == currentM.teamA.teamId) currentM.teamA else currentM.teamB
            val bowlTeam2 = if (inn2?.bowlingTeamId == currentM.teamA.teamId) currentM.teamA else currentM.teamB

            val defaultStriker2 = batTeam2.players.getOrNull(0)?.name ?: if (batTeam2.teamName.isNotBlank()) "${batTeam2.teamName} Player 1" else "Player 1"
            val defaultNonStriker2 = batTeam2.players.getOrNull(1)?.name ?: if (batTeam2.teamName.isNotBlank()) "${batTeam2.teamName} Player 2" else "Player 2"
            val defaultBowler2 = bowlTeam2.players.getOrNull(0)?.name ?: if (bowlTeam2.teamName.isNotBlank()) "${bowlTeam2.teamName} Bowler 1" else "Bowler 1"

            _overrideStrikerId.value = defaultStriker2
            _overrideNonStrikerId.value = defaultNonStriker2
            _overrideBowlerId.value = defaultBowler2

            val updatedMatch = currentM.copy(
                currentInningsNumber = 2,
                currentStrikerId = defaultStriker2,
                currentNonStrikerId = defaultNonStriker2,
                currentBowlerId = defaultBowler2,
                updatedAt = System.currentTimeMillis()
            )
            _uiState.value = _uiState.value.copy(
                showInningsBreakDialog = false,
                currentMatch = updatedMatch
            )
            viewModelScope.launch {
                scoringRepository.syncInningsState(updatedMatch.matchId, updatedMatch)
            }
        }
    }

    fun openInningsBreakDialog() {
        _uiState.value = _uiState.value.copy(showInningsBreakDialog = true)
    }

    fun dismissMatchCompletedDialog() {
        hasUserDismissedMatchCompleted = true
        _uiState.value = _uiState.value.copy(showMatchCompletedDialog = false)
    }

    fun openMatchCompletedDialog() {
        _uiState.value = _uiState.value.copy(showMatchCompletedDialog = true)
    }
}
