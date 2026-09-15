package com.cricpro.app.presentation.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.domain.model.Team
import com.cricpro.app.domain.model.TossDecision
import com.cricpro.app.presentation.team.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(
    onMatchCreated: (String) -> Unit,
    onNavigateBack: () -> Unit,
    teamViewModel: TeamViewModel = hiltViewModel(),
    matchViewModel: MatchViewModel = hiltViewModel()
) {
    val teams by teamViewModel.teams.collectAsState()
    val isCreating by matchViewModel.isCreating.collectAsState()

    var matchTitle by remember { mutableStateOf("") }
    var selectedTeamA by remember { mutableStateOf<Team?>(null) }
    var selectedTeamB by remember { mutableStateOf<Team?>(null) }
    var oversText by remember { mutableStateOf("20") }
    var groundName by remember { mutableStateOf("Lords") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Match", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = matchTitle, onValueChange = { matchTitle = it }, label = { Text("Match Title (e.g. T20 Final)") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(value = groundName, onValueChange = { groundName = it }, label = { Text("Ground Venue") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(value = oversText, onValueChange = { oversText = it }, label = { Text("Overs per Innings") }, modifier = Modifier.fillMaxWidth())

            Text("Select Team A", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(teams) { team ->
                    FilterChip(
                        selected = selectedTeamA?.teamId == team.teamId,
                        onClick = { selectedTeamA = team },
                        label = { Text(team.teamName) }
                    )
                }
            }

            Text("Select Team B", fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(teams) { team ->
                    FilterChip(
                        selected = selectedTeamB?.teamId == team.teamId,
                        onClick = { selectedTeamB = team },
                        label = { Text(team.teamName) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (isCreating) return@Button
                    val tA = selectedTeamA ?: Team(teamId = "t1", teamName = "Team Alpha")
                    val tB = selectedTeamB ?: Team(teamId = "t2", teamName = "Team Beta")
                    val overs = oversText.toIntOrNull() ?: 20
                    matchViewModel.createMatch(
                        title = matchTitle,
                        groundName = groundName,
                        overs = overs,
                        teamA = tA,
                        teamB = tB,
                        onMatchCreated = onMatchCreated
                    )
                },
                enabled = !isCreating,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Proceed to Toss", fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TossScreen(
    matchId: String,
    onTossComplete: () -> Unit,
    viewModel: ScoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val match = uiState.currentMatch

    var selectedTossWinnerId by remember { mutableStateOf<String?>(null) }
    var selectedDecision by remember { mutableStateOf(TossDecision.BAT) }
    var isTossSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Match Toss", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Who Won The Toss?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(
                    selected = selectedTossWinnerId == (match?.teamA?.teamId ?: "t1"),
                    onClick = { selectedTossWinnerId = match?.teamA?.teamId ?: "t1" },
                    label = { Text(match?.teamA?.teamName ?: "Team A") }
                )
                FilterChip(
                    selected = selectedTossWinnerId == (match?.teamB?.teamId ?: "t2"),
                    onClick = { selectedTossWinnerId = match?.teamB?.teamId ?: "t2" },
                    label = { Text(match?.teamB?.teamName ?: "Team B") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Decision?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(selected = selectedDecision == TossDecision.BAT, onClick = { selectedDecision = TossDecision.BAT }, label = { Text("BAT FIRST") })
                FilterChip(selected = selectedDecision == TossDecision.BOWL, onClick = { selectedDecision = TossDecision.BOWL }, label = { Text("BOWL FIRST") })
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (isTossSubmitting) return@Button
                    isTossSubmitting = true
                    val winnerId = selectedTossWinnerId ?: (match?.teamA?.teamId ?: "t1")
                    viewModel.selectToss(winnerId, selectedDecision)
                    onTossComplete()
                },
                enabled = !isTossSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isTossSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Start Live Scoring", fontSize = 16.sp)
                }
            }
        }
    }
}
