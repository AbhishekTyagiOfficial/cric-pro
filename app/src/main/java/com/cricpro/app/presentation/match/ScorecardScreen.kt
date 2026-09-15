package com.cricpro.app.presentation.match

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.domain.model.*
import com.cricpro.app.utils.ExcelExporter
import com.cricpro.app.utils.PdfExporter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScorecardScreen(
    matchId: String,
    onNavigateBack: () -> Unit,
    viewModel: ScoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val match = uiState.currentMatch
    val context = LocalContext.current

    var selectedInningsTab by remember { mutableIntStateOf(0) }

    val inn1 = match?.firstInnings
    val inn2 = match?.secondInnings

    val hasInn2 = inn2 != null && (inn2.totalRuns > 0 || inn2.legalBallsBowled > 0 || inn2.wickets > 0 || match.currentInningsNumber == 2)

    LaunchedEffect(match?.currentInningsNumber, hasInn2) {
        if (hasInn2 && match?.currentInningsNumber == 2) {
            selectedInningsTab = 1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Full Scorecard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { match?.let { PdfExporter.exportMatchScorecard(context, it) } }) {
                        Icon(Icons.Default.PictureAsPdf, "PDF Export")
                    }
                    IconButton(onClick = { match?.let { ExcelExporter.exportMatchToCsv(context, it) } }) {
                        Icon(Icons.Default.Download, "Excel Export")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            val currentInningsToDisplay = if (selectedInningsTab == 0 || !hasInn2) inn1 else inn2
            val battingTeam = if (currentInningsToDisplay?.battingTeamId == match?.teamA?.teamId) match?.teamA else match?.teamB
            val bowlingTeam = if (currentInningsToDisplay?.bowlingTeamId == match?.teamA?.teamId) match?.teamA else match?.teamB

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Equation header (e.g., Dublin Guardians need 162 runs in 72 balls)
                if (match != null && inn2 != null && !inn2.isCompleted && match.currentInningsNumber == 2 && inn2.target != null) {
                    val runsNeeded = (inn2.target - inn2.totalRuns).coerceAtLeast(0)
                    val maxBalls = match.totalOvers * 6
                    val ballsRemaining = (maxBalls - inn2.legalBallsBowled).coerceAtLeast(0)
                    val batTeamName = if (inn2.battingTeamId == match.teamA.teamId) match.teamA.teamName else match.teamB.teamName

                    Text(
                        text = "$batTeamName need $runsNeeded runs in $ballsRemaining balls",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD81B60)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else if (match != null && !match.resultMessage.isNullOrBlank()) {
                    Text(
                        text = match.resultMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00875A)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Innings Pill Chips (e.g. DBG (2nd Inn) | ADF (1st Inn))
                if (hasInn2 && match != null) {
                    val team1Name = if (inn1?.battingTeamId == match.teamA.teamId) match.teamA.teamName else match.teamB.teamName
                    val team2Name = if (inn2?.battingTeamId == match.teamA.teamId) match.teamA.teamName else match.teamB.teamName
                    val code1 = getTeamShortCode(team1Name)
                    val code2 = getTeamShortCode(team2Name)

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        PillChip(
                            text = "$code1 (1st Inn)",
                            isSelected = selectedInningsTab == 0,
                            onClick = { selectedInningsTab = 0 }
                        )
                        PillChip(
                            text = "$code2 (2nd Inn)",
                            isSelected = selectedInningsTab == 1,
                            onClick = { selectedInningsTab = 1 }
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            if (currentInningsToDisplay != null && match != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ScorecardTableCard(
                            match = match,
                            innings = currentInningsToDisplay,
                            battingTeam = battingTeam ?: Team(),
                            bowlingTeam = bowlingTeam ?: Team()
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Scorecard Data Available", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun PillChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF00875A) else Color(0xFFE0E0E0))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF333333),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getTeamShortCode(teamName: String): String {
    if (teamName.isBlank()) return "TEAM"
    val words = teamName.trim().split("\\s+".toRegex())
    if (words.size >= 3) {
        return "${words[0][0]}${words[1][0]}${words[2][0]}".uppercase()
    } else if (words.size == 2) {
        return "${words[0].take(2)}${words[1][0]}".uppercase()
    } else if (teamName.length >= 3) {
        return teamName.take(3).uppercase()
    }
    return teamName.uppercase()
}

@Composable
fun ScorecardTableCard(
    match: Match,
    innings: Innings,
    battingTeam: Team,
    bowlingTeam: Team
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Green Banner: Team Name + Score (e.g. Dublin Guardians  53-3 (8 Ov))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00875A))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = battingTeam.teamName.ifEmpty { "Batting Team" },
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${innings.totalRuns}-${innings.wickets} (${innings.oversFormatted} Ov)",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Batting Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFECECEC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Batter", modifier = Modifier.weight(3.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333))
                Text("R", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("B", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("4s", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("6s", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("SR", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Batting Rows
            val battersList = innings.batters.values.toList()
            battersList.forEach { batter ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(3.2f)) {
                        Text(
                            text = batter.name.ifEmpty { batter.playerId },
                            color = Color(0xFF1A73E8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        val dismissal = if (batter.isOut) {
                            if (batter.dismissalInfo.isNotBlank() && batter.dismissalInfo != "not out") batter.dismissalInfo else "out"
                        } else {
                            "batting"
                        }
                        Text(
                            text = dismissal,
                            color = Color(0xFF757575),
                            fontSize = 12.sp
                        )
                    }
                    Text(text = "${batter.runs}", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.End)
                    Text(text = "${batter.balls}", modifier = Modifier.weight(0.8f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    Text(text = "${batter.fours}", modifier = Modifier.weight(0.8f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    Text(text = "${batter.sixes}", modifier = Modifier.weight(0.8f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    val srStr = String.format(Locale.US, "%.2f", batter.strikeRate)
                    Text(text = srStr, modifier = Modifier.weight(1.2f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                }
                HorizontalDivider(color = Color(0xFFE0E0E0))
            }

            // Extras Row
            val ex = innings.extras
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Extras", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Text(
                    text = "${ex.total} (b ${ex.byes}, lb ${ex.legByes}, w ${ex.wides}, nb ${ex.noBalls}, p 0)",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = Color(0xFF333333)
                )
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Total Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Text(
                    text = "${innings.totalRuns}-${innings.wickets} (${innings.oversFormatted} Overs, RR: ${innings.runRateFormatted})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Yet to Bat Row
            val battedPlayerIdsAndNames = innings.batters.values.flatMap { listOf(it.playerId.lowercase(), it.name.lowercase()) }.toSet()
            val yetToBatPlayers = battingTeam.players.filter { player ->
                !battedPlayerIdsAndNames.contains(player.playerId.lowercase()) &&
                !battedPlayerIdsAndNames.contains(player.name.lowercase())
            }

            if (yetToBatPlayers.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Yet to Bat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = yetToBatPlayers.joinToString(", ") { it.name },
                        color = Color(0xFF1A73E8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(color = Color(0xFFE0E0E0))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bowling Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFECECEC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Bowler", modifier = Modifier.weight(3.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333))
                Text("O", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("M", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("R", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("W", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("NB", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("WD", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                Text("ECO", modifier = Modifier.weight(1.1f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Bowling Rows
            val bowlersList = innings.bowlers.values.toList()
            bowlersList.forEach { bowler ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = bowler.name.ifEmpty { bowler.playerId },
                        modifier = Modifier.weight(3.0f),
                        color = Color(0xFF1A73E8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(text = bowler.oversFormatted, modifier = Modifier.weight(0.7f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    Text(text = "${bowler.maidens}", modifier = Modifier.weight(0.7f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    Text(text = "${bowler.runsConceded}", modifier = Modifier.weight(0.7f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    Text(text = "${bowler.wickets}", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.End)
                    Text(text = "${bowler.noBalls}", modifier = Modifier.weight(0.7f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    Text(text = "${bowler.wides}", modifier = Modifier.weight(0.7f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                    val ecoStr = String.format(Locale.US, "%.2f", bowler.economy)
                    Text(text = ecoStr, modifier = Modifier.weight(1.1f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                }
                HorizontalDivider(color = Color(0xFFE0E0E0))
            }

            // Fall of Wickets Section
            if (innings.fallOfWickets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFECECEC))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fall of Wickets", modifier = Modifier.weight(3.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333))
                    Text("Score", modifier = Modifier.weight(1.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.Center)
                    Text("Over", modifier = Modifier.weight(1.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
                }

                HorizontalDivider(color = Color(0xFFE0E0E0))

                innings.fallOfWickets.forEach { fow ->
                    val resolvedName = innings.batters[fow.dismissedPlayerName]?.name
                        ?: battingTeam.players.find { it.playerId == fow.dismissedPlayerName || it.name == fow.dismissedPlayerName }?.name
                        ?: fow.dismissedPlayerName

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = resolvedName,
                            modifier = Modifier.weight(3.0f),
                            color = Color(0xFF1A73E8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${fow.score}-${fow.wicketNumber}",
                            modifier = Modifier.weight(1.0f),
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = fow.oversFormatted,
                            modifier = Modifier.weight(1.0f),
                            fontSize = 14.sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                }
            }

            // Powerplays Section
            val ppOversEnd = minOf(6, match.totalOvers)
            val ppBalls = innings.ballsHistory.filter { it.isLegalDelivery && (it.totalLegalBallsInInnings <= ppOversEnd * 6) }
            val ppRuns = if (ppBalls.isNotEmpty()) {
                ppBalls.sumOf { it.runsScored + it.extraRuns }
            } else if (innings.legalBallsBowled >= ppOversEnd * 6) {
                (innings.totalRuns * (ppOversEnd.toDouble() / maxOf(1, match.totalOvers))).toInt()
            } else {
                innings.totalRuns
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFECECEC))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Powerplays", modifier = Modifier.weight(3.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333))
                Text("Overs", modifier = Modifier.weight(1.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.Center)
                Text("Runs", modifier = Modifier.weight(1.0f), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333), textAlign = TextAlign.End)
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mandatory", modifier = Modifier.weight(3.0f), fontSize = 14.sp, color = Color(0xFF333333))
                Text("0.1 - $ppOversEnd", modifier = Modifier.weight(1.0f), fontSize = 14.sp, color = Color(0xFF333333), textAlign = TextAlign.Center)
                Text("$ppRuns", modifier = Modifier.weight(1.0f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.End)
            }
            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Partnerships Section
            if (innings.partnerships.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFECECEC))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Partnerships", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF333333))
                }

                HorizontalDivider(color = Color(0xFFE0E0E0))

                innings.partnerships.forEach { p ->
                    val p1Name = innings.batters[p.player1Id]?.name
                        ?: battingTeam.players.find { it.playerId == p.player1Id || it.name == p.player1Id }?.name
                        ?: p.player1Id
                    val p2Name = innings.batters[p.player2Id]?.name
                        ?: battingTeam.players.find { it.playerId == p.player2Id || it.name == p.player2Id }?.name
                        ?: p.player2Id

                    val p1LastName = p1Name.trim().split("\\s+".toRegex()).lastOrNull() ?: p1Name
                    val p2LastName = p2Name.trim().split("\\s+".toRegex()).lastOrNull() ?: p2Name

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p1LastName,
                            color = Color(0xFF1A73E8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1.2f)
                        )
                        Text(
                            text = "${p.runs}(${p.balls})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1.0f)
                        )
                        Text(
                            text = p2LastName,
                            color = Color(0xFF1A73E8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                }
            }
        }
    }
}
