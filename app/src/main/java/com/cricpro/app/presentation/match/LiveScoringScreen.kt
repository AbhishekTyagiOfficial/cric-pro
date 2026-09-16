package com.cricpro.app.presentation.match

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.domain.model.Ball
import com.cricpro.app.domain.model.BatterScore
import com.cricpro.app.domain.model.ExtraType
import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.WicketType
import com.cricpro.app.presentation.components.AdBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveScoringScreen(
    matchId: String,
    onNavigateToHome: () -> Unit = {},
    onNavigateToScorecard: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    viewModel: ScoringViewModel = hiltViewModel()
) {
    BackHandler { onNavigateToHome() }

    val uiState by viewModel.uiState.collectAsState()
    val match = uiState.currentMatch
    fun isTeamA(idOrName: String?, teamA: com.cricpro.app.domain.model.Team?, teamB: com.cricpro.app.domain.model.Team?): Boolean {
        if (idOrName.isNullOrBlank()) return true
        if (teamB != null && (idOrName == teamB.teamId || (teamB.teamName.isNotBlank() && idOrName.equals(teamB.teamName, ignoreCase = true)))) {
            return false
        }
        if (teamA != null && (idOrName == teamA.teamId || (teamA.teamName.isNotBlank() && idOrName.equals(teamA.teamName, ignoreCase = true)))) {
            return true
        }
        return true
    }

    val currentInnings = if (match?.currentInningsNumber == 1) match?.firstInnings else match?.secondInnings
    val isBattingTeamA = isTeamA(currentInnings?.battingTeamId, match?.teamA, match?.teamB)
    val battingTeam = if (isBattingTeamA) match?.teamA else match?.teamB
    val bowlingTeam = if (isBattingTeamA) match?.teamB else match?.teamA

    val battingTeamName = battingTeam?.teamName ?: "Batting Team"
    val bowlingTeamName = bowlingTeam?.teamName ?: "Bowling Team"

    val battingPlayers = battingTeam?.players ?: emptyList()
    val bowlingPlayers = bowlingTeam?.players ?: emptyList()

    var selectedExtra by remember { mutableStateOf(ExtraType.NONE) }
    var showWicketDialog by remember { mutableStateOf(false) }
    var showStrikerDialog by remember { mutableStateOf(false) }
    var showNonStrikerDialog by remember { mutableStateOf(false) }
    var showBowlerDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val isMatchCompleted = match?.status == com.cricpro.app.domain.model.MatchStatus.COMPLETED
    val legalBalls = currentInnings?.legalBallsBowled ?: 0
    val totalWickets = currentInnings?.wickets ?: 0
    val isInningsFinished = currentInnings?.isCompleted == true
    val completedOverCount = legalBalls / 6
    val isOverJustCompleted = legalBalls > 0 && legalBalls % 6 == 0 && currentInnings?.ballsHistory?.lastOrNull()?.isLegalDelivery == true && !isMatchCompleted && !isInningsFinished

    var lastHandledOverIndex by remember { mutableStateOf(completedOverCount) }
    var lastHandledWicketCount by remember { mutableStateOf(totalWickets) }

    LaunchedEffect(legalBalls, isMatchCompleted, isInningsFinished) {
        if (isOverJustCompleted && completedOverCount > lastHandledOverIndex) {
            lastHandledOverIndex = completedOverCount
            showBowlerDialog = true
        } else if (completedOverCount < lastHandledOverIndex) {
            lastHandledOverIndex = completedOverCount
        }
    }

    LaunchedEffect(totalWickets, isMatchCompleted, isInningsFinished) {
        if (totalWickets > 0 && totalWickets > lastHandledWicketCount && !isMatchCompleted && !isInningsFinished) {
            lastHandledWicketCount = totalWickets
            val lastBall = currentInnings?.ballsHistory?.lastOrNull()
            if (lastBall != null && lastBall.wicketType != com.cricpro.app.domain.model.WicketType.NONE) {
                val dismissedId = lastBall.dismissedPlayerId ?: lastBall.strikerId
                if (dismissedId == match?.currentNonStrikerId) {
                    showNonStrikerDialog = true
                } else {
                    showStrikerDialog = true
                }
            }
        } else if (totalWickets < lastHandledWicketCount) {
            lastHandledWicketCount = totalWickets
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(match?.title ?: "Sher ki team vs dushman ki team", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Home")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToScorecard) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Scorecard")
                    }
                    IconButton(onClick = onNavigateToAnalytics) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF3EDF7),
                    titleContentColor = Color(0xFF1D1B20)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            if (isMatchCompleted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "MATCH COMPLETED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = match?.resultMessage.takeIf { !it.isNullOrBlank() } ?: "Match Completed!",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFF1B5E20),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openMatchCompletedDialog() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Match Summary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToScorecard,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1B5E20)),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Full Scorecard",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Match Header Banner (Green Card as in user screenshot)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Innings ${match?.currentInningsNumber ?: 1}: $battingTeamName",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentInnings?.totalRuns ?: 0}/${currentInnings?.wickets ?: 0}",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Overs: ${currentInnings?.oversFormatted ?: "0.0"} / ${match?.totalOvers ?: 20}",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "CRR: ${currentInnings?.runRateFormatted ?: "0.00"}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Batting (Striker & Non-Striker) Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Batting (Striker & Non-Striker)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF2E7D32)
                        )
                        OutlinedButton(
                            onClick = { viewModel.swapStrike() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Swap Strike", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFE7E0EC))
                    Spacer(modifier = Modifier.height(8.dp))

                    fun getBatterScore(name: String?): BatterScore? {
                        if (name.isNullOrBlank()) return null
                        return currentInnings?.batters?.get(name)
                            ?: currentInnings?.batters?.values?.find { it.name == name || it.playerId == name }
                    }

                    val strikerScore = getBatterScore(match?.currentStrikerId)
                    val nonStrikerScore = getBatterScore(match?.currentNonStrikerId)

                    // Striker Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!isMatchCompleted) showStrikerDialog = true }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏏 ${match?.currentStrikerId ?: "Striker"} *",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1D1B20)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${strikerScore?.runs ?: 0} (${strikerScore?.balls ?: 0}b)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Edit, contentDescription = "Edit Striker", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        }
                    }

                    // Non-Striker Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!isMatchCompleted) showNonStrikerDialog = true }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏏 ${match?.currentNonStrikerId ?: "Non-Striker"}",
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            color = Color(0xFF49454F)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${nonStrikerScore?.runs ?: 0} (${nonStrikerScore?.balls ?: 0}b)",
                                fontSize = 15.sp,
                                color = Color(0xFF49454F)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Edit, contentDescription = "Edit Non-Striker", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Bowling (Current Bowler) Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bowling (Current Bowler)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF2E7D32)
                        )
                        OutlinedButton(
                            onClick = { if (!isMatchCompleted) showBowlerDialog = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change Bowler", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFE7E0EC))
                    Spacer(modifier = Modifier.height(8.dp))

                    val bowlerScore = currentInnings?.bowlers?.get(match?.currentBowlerId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!isMatchCompleted) showBowlerDialog = true }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🥎 ${match?.currentBowlerId ?: "Bowler"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1D1B20)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${bowlerScore?.wickets ?: 0}/${bowlerScore?.runsConceded ?: 0} (${bowlerScore?.oversFormatted ?: "0.0"} ov)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D1B20)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Edit, contentDescription = "Edit Bowler", tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // This Over (Ball by Ball) Card - Directly matching user screenshot!
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "This Over (Ball by Ball)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val ballsInCurrentInnings = currentInnings?.ballsHistory ?: emptyList()
                    val legalCount = currentInnings?.legalBallsBowled ?: 0
                    val ballsInThisOver = if (ballsInCurrentInnings.isNotEmpty()) {
                        val legalInCurrentOver = legalCount % 6
                        val ballsToTake = if (legalInCurrentOver == 0 && legalCount > 0) 6 else legalInCurrentOver
                        var takenLegal = 0
                        val overBalls = mutableListOf<Ball>()
                        for (ball in ballsInCurrentInnings.reversed()) {
                            overBalls.add(0, ball)
                            if (ball.isLegalDelivery) {
                                takenLegal++
                                if (takenLegal >= ballsToTake) break
                            }
                        }
                        overBalls
                    } else emptyList()

                    if (ballsInThisOver.isEmpty()) {
                        Text("No balls bowled in this over yet", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ballsInThisOver.forEach { ball ->
                                val (bText, bBg) = when {
                                    ball.wicketType != WicketType.NONE -> Pair("W", Color(0xFFD32F2F))
                                    ball.extraType == ExtraType.WIDE -> Pair("${ball.totalRunsOnBall}WD", Color(0xFFF57C00))
                                    ball.extraType == ExtraType.NO_BALL -> Pair("${ball.totalRunsOnBall}NB", Color(0xFFE65100))
                                    ball.extraType == ExtraType.BYE -> Pair("${ball.extraRuns}B", Color(0xFF7CB342))
                                    ball.extraType == ExtraType.LEG_BYE -> Pair("${ball.extraRuns}LB", Color(0xFF7CB342))
                                    ball.runsScored == 6 -> Pair("6", Color(0xFF2E7D32))
                                    ball.runsScored == 4 -> Pair("4", Color(0xFF1565C0))
                                    else -> Pair("${ball.runsScored}", Color(0xFF1E88E5)) // Bright blue circular badge as in user screenshot!
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = bBg,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = bText,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Score Delivery Actions Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Extras Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("WD", "NB", "BYE", "LB").forEach { ext ->
                            val eType = when(ext) {
                                "WD" -> ExtraType.WIDE
                                "NB" -> ExtraType.NO_BALL
                                "BYE" -> ExtraType.BYE
                                else -> ExtraType.LEG_BYE
                            }
                            FilterChip(
                                selected = selectedExtra == eType,
                                onClick = { selectedExtra = if (selectedExtra == eType) ExtraType.NONE else eType },
                                label = { Text(ext, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { if (!isMatchCompleted) showWicketDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
                        ) {
                            Text("WICKET", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // Runs Grid Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf(0, 1, 2, 3, 4, 5, 6).forEach { run ->
                            val btnColor = when (run) {
                                4, 6 -> Color(0xFF00C853) // Green for 4 and 6 as in user screenshot!
                                else -> Color(0xFFE8DEF8)
                            }
                            val txtColor = when (run) {
                                4, 6 -> Color.White
                                else -> Color(0xFF1D1B20)
                            }
                            Surface(
                                onClick = {
                                    if (!isMatchCompleted) {
                                        viewModel.scoreRuns(run, selectedExtra)
                                        selectedExtra = ExtraType.NONE
                                    }
                                },
                                shape = CircleShape,
                                color = btnColor,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("$run", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = txtColor)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.undoLastBall() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Undo Last Ball", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showWicketDialog && !isMatchCompleted) {
        AlertDialog(
            onDismissRequest = { showWicketDialog = false },
            title = { Text("Select Wicket Type") },
            text = {
                Column {
                    WicketType.values().filter { it != WicketType.NONE }.forEach { type ->
                        TextButton(
                            onClick = {
                                viewModel.scoreRuns(0, selectedExtra, type)
                                selectedExtra = ExtraType.NONE
                                showWicketDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(type.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showWicketDialog = false }) { Text("Cancel") } }
        )
    }

    if (showStrikerDialog && !isMatchCompleted) {
        PlayerSelectionDialog(
            title = "Select Striker (Batter)",
            teamName = battingTeamName,
            teamPlayers = battingPlayers,
            currentSelected = match?.currentStrikerId ?: "Player 1",
            currentStriker = match?.currentStrikerId,
            currentNonStriker = match?.currentNonStrikerId,
            previousBowler = match?.currentBowlerId,
            existingBatters = currentInnings?.batters ?: emptyMap(),
            onPlayerSelected = { viewModel.selectStriker(it); showStrikerDialog = false },
            onDismiss = { showStrikerDialog = false }
        )
    }

    if (showNonStrikerDialog && !isMatchCompleted) {
        PlayerSelectionDialog(
            title = "Select Non-Striker (Batter)",
            teamName = battingTeamName,
            teamPlayers = battingPlayers,
            currentSelected = match?.currentNonStrikerId ?: "Player 2",
            currentStriker = match?.currentStrikerId,
            currentNonStriker = match?.currentNonStrikerId,
            previousBowler = match?.currentBowlerId,
            existingBatters = currentInnings?.batters ?: emptyMap(),
            onPlayerSelected = { viewModel.selectNonStriker(it); showNonStrikerDialog = false },
            onDismiss = { showNonStrikerDialog = false }
        )
    }

    if (showBowlerDialog && !isMatchCompleted) {
        val overTitle = if (isOverJustCompleted) "Over $completedOverCount Complete! Select Bowler (Over ${completedOverCount + 1})" else "Select Bowler"
        PlayerSelectionDialog(
            title = overTitle,
            teamName = bowlingTeamName,
            teamPlayers = bowlingPlayers,
            currentSelected = match?.currentBowlerId ?: "Bowler 1",
            currentStriker = match?.currentStrikerId,
            currentNonStriker = match?.currentNonStrikerId,
            previousBowler = match?.currentBowlerId,
            onPlayerSelected = { viewModel.selectBowler(it); showBowlerDialog = false },
            onDismiss = { showBowlerDialog = false }
        )
    }

    if (uiState.showInningsBreakDialog) {
        val firstInningsRuns = match?.firstInnings?.totalRuns ?: 0
        val targetRuns = firstInningsRuns + 1
        val battingTeam2Name = if (match?.secondInnings?.battingTeamId == match?.teamA?.teamId) match?.teamA?.teamName else match?.teamB?.teamName

        AlertDialog(
            onDismissRequest = { viewModel.dismissInningsBreakDialog() },
            title = { Text("Innings Break! Start 2nd Innings", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("1st Innings Score: $firstInningsRuns runs", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target for $battingTeam2Name: $targetRuns runs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap 'Start 2nd Innings' to begin the chase.", style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.dismissInningsBreakDialog() }) {
                    Text("Start 2nd Innings", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissInningsBreakDialog() }) {
                    Text("Dismiss")
                }
            }
        )
    }

    if (uiState.showMatchCompletedDialog) {
        val inn1 = match?.firstInnings
        val inn2 = match?.secondInnings
        val isTied = match?.resultMessage?.contains("Tied", ignoreCase = true) == true ||
                     (inn1 != null && inn2 != null && inn1.totalRuns == inn2.totalRuns && inn2.isCompleted)
        val dialogTitle = if (isTied) "Match Tied!" else "Match Completed!"
        val resultMessage = match?.resultMessage.takeIf { !it.isNullOrBlank() }
            ?: if (isTied) "Match Tied!" else "Match Completed!"

        val team1Name = match?.teamA?.teamName.takeIf { !it.isNullOrBlank() } ?: "Team A"
        val team2Name = match?.teamB?.teamName.takeIf { !it.isNullOrBlank() } ?: "Team B"

        val inn1BattingName = if (inn1?.battingTeamId == match?.teamA?.teamId) team1Name else team2Name
        val inn2BattingName = if (inn2?.battingTeamId == match?.teamA?.teamId) team1Name else team2Name

        AlertDialog(
            onDismissRequest = { viewModel.dismissMatchCompletedDialog() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = dialogTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF1B5E20)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = resultMessage,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Match Summary",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (inn1 != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$inn1BattingName (1st Inn):",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${inn1.totalRuns}/${inn1.wickets} (${inn1.oversFormatted} ov)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (inn2 != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$inn2BattingName (2nd Inn):",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${inn2.totalRuns}/${inn2.wickets} (${inn2.oversFormatted} ov)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissMatchCompletedDialog()
                        onNavigateToScorecard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Scorecard", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissMatchCompletedDialog()
                        onNavigateToHome()
                    }
                ) {
                    Text("Go to Home")
                }
            }
        )
    }
}

@Composable
fun PlayerSelectionDialog(
    title: String,
    teamName: String = "",
    teamPlayers: List<Player> = emptyList(),
    currentSelected: String = "",
    currentStriker: String? = null,
    currentNonStriker: String? = null,
    previousBowler: String? = null,
    existingBatters: Map<String, BatterScore> = emptyMap(),
    onPlayerSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (teamName.isNotEmpty()) {
                    Text("Team: $teamName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                val availablePlayers = if (teamPlayers.isNotEmpty()) {
                    teamPlayers.map { it.name }
                } else {
                    List(11) { if (teamName.isNotBlank()) "$teamName Player ${it + 1}" else "Player ${it + 1}" }
                }
                LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                    items(availablePlayers) { player ->
                        val isStriker = player == currentStriker
                        val isNonStriker = player == currentNonStriker
                        val isPreviousBowler = player == previousBowler
                        val isOut = existingBatters[player]?.isOut == true
                        val isDisabled = isStriker || isNonStriker || isPreviousBowler || isOut

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isDisabled) { onPlayerSelected(player) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = player,
                                color = when {
                                    isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                    player == currentSelected -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (player == currentSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            if (isOut) {
                                Text("(OUT)", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            } else if (isStriker || isNonStriker) {
                                Text("(BATTING)", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                            } else if (isPreviousBowler) {
                                Text("(PREV BOWLER)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
