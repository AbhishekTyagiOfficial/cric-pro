package com.cricpro.app.presentation.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            // Match Result & Title Banner
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = match?.title?.ifEmpty { "Match Scorecard" } ?: "Match Scorecard",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF202124)
                )
                if (!match?.resultMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = match?.resultMessage ?: "",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00875A)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFE0E0E0))

            // Innings Tab Selector if 2nd innings exists
            if (hasInn2) {
                TabRow(
                    selectedTabIndex = selectedInningsTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF00875A)
                ) {
                    val team1Name = if (inn1?.battingTeamId == match?.teamA?.teamId) match?.teamA?.teamName else match?.teamB?.teamName
                    val team2Name = if (inn2?.battingTeamId == match?.teamA?.teamId) match?.teamA?.teamName else match?.teamB?.teamName

                    Tab(
                        selected = selectedInningsTab == 0,
                        onClick = { selectedInningsTab = 0 },
                        text = { Text("1st Innings (${team1Name ?: "Team 1"})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedInningsTab == 1,
                        onClick = { selectedInningsTab = 1 },
                        text = { Text("2nd Innings (${team2Name ?: "Team 2"})", fontWeight = FontWeight.Bold) }
                    )
                }
            }

            val currentInningsToDisplay = if (selectedInningsTab == 0 || !hasInn2) inn1 else inn2
            val battingTeam = if (currentInningsToDisplay?.battingTeamId == match?.teamA?.teamId) match?.teamA else match?.teamB
            val bowlingTeam = if (currentInningsToDisplay?.bowlingTeamId == match?.teamA?.teamId) match?.teamA else match?.teamB

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
            // Top Green Banner: Team Name + Score (e.g. Amsterdam Flames  214-5 (20 Ov))
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
                            "not out"
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
        }
    }
}
