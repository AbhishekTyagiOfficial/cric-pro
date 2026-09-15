package com.cricpro.app.presentation.tournament

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.domain.model.PointsTableEntry
import com.cricpro.app.domain.model.TournamentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListScreen(
    onNavigateToTournamentDetail: (String) -> Unit,
    viewModel: TournamentViewModel = hiltViewModel()
) {
    val tournaments by viewModel.tournaments.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var tourName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tournaments", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, "Create Tournament")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tournaments) { tour ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToTournamentDetail(tour.tournamentId) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(tour.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Type: ${tour.type.name}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create New Tournament") },
                text = {
                    OutlinedTextField(value = tourName, onValueChange = { tourName = it }, label = { Text("Tournament Name") }, modifier = Modifier.fillMaxWidth())
                },
                confirmButton = {
                    Button(onClick = {
                        if (tourName.isNotBlank()) {
                            viewModel.createTournament(tourName, TournamentType.LEAGUE)
                            tourName = ""
                            showCreateDialog = false
                        }
                    }) { Text("Create") }
                },
                dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsTableScreen(
    entries: List<PointsTableEntry>,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Points Table", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Header Row
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Team", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                Text("P", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
                Text("W", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
                Text("L", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
                Text("Pts", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text("NRR", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold)
            }
            Divider()

            LazyColumn {
                items(entries) { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.teamName, modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold)
                        Text("${item.played}", modifier = Modifier.weight(0.8f))
                        Text("${item.won}", modifier = Modifier.weight(0.8f))
                        Text("${item.lost}", modifier = Modifier.weight(0.8f))
                        Text("${item.points}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text(String.format("%.3f", item.netRunRate), modifier = Modifier.weight(1.2f))
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}
