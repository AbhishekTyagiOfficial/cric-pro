package com.cricpro.app.presentation.team

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.Team

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamListScreen(
    onNavigateToTeamDetail: (String) -> Unit,
    viewModel: TeamViewModel = hiltViewModel()
) {
    val teams by viewModel.teams.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var newTeamName by remember { mutableStateOf("") }
    var teamToDelete by remember { mutableStateOf<Team?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Teams", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, "Create Team")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(teams) { team ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onNavigateToTeamDetail(team.teamId) },
                            onLongClick = { teamToDelete = team }
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(team.teamName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("${team.players.size} Players", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { teamToDelete = team }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete Team",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Create New Team") },
                text = {
                    OutlinedTextField(
                        value = newTeamName,
                        onValueChange = { newTeamName = it },
                        label = { Text("Team Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createTeam(newTeamName)
                        newTeamName = ""
                        showCreateDialog = false
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
                }
            )
        }

        teamToDelete?.let { team ->
            AlertDialog(
                onDismissRequest = { teamToDelete = null },
                title = { Text("Delete Team?") },
                text = { Text("Are you sure you want to permanently delete '${team.teamName}' and all its players? This will remove the team from both local database and Firebase.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTeam(team.teamId)
                            teamToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { teamToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    teamId: String,
    onNavigateBack: () -> Unit,
    viewModel: TeamViewModel = hiltViewModel()
) {
    val teams by viewModel.teams.collectAsState()
    val team = teams.find { it.teamId == teamId }

    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var playerName by remember { mutableStateOf("") }
    var selectedPlayerForAction by remember { mutableStateOf<Player?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(team?.teamName ?: "Team Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddPlayerDialog = true }) {
                Icon(Icons.Default.PersonAdd, "Add Player")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Squad (${team?.players?.size ?: 0})", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(team?.players ?: emptyList()) { player ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(player.name, fontWeight = FontWeight.Bold)
                                        if (player.isCaptain) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("C") }
                                        }
                                        if (player.isViceCaptain) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Badge(containerColor = MaterialTheme.colorScheme.secondary) { Text("VC") }
                                        }
                                    }
                                    Text(player.role.name, fontSize = 12.sp)
                                }
                            }
                            IconButton(onClick = { selectedPlayerForAction = player }) {
                                Icon(Icons.Default.MoreVert, "Actions")
                            }
                        }
                    }
                }
            }
        }

        if (showAddPlayerDialog) {
            AlertDialog(
                onDismissRequest = { showAddPlayerDialog = false },
                title = { Text("Add Player to Squad") },
                text = {
                    OutlinedTextField(value = playerName, onValueChange = { playerName = it }, label = { Text("Player Name") }, modifier = Modifier.fillMaxWidth())
                },
                confirmButton = {
                    Button(onClick = {
                        if (playerName.isNotBlank()) {
                            viewModel.addPlayerToTeam(teamId, Player(name = playerName))
                            playerName = ""
                            showAddPlayerDialog = false
                        }
                    }) { Text("Add") }
                },
                dismissButton = { TextButton(onClick = { showAddPlayerDialog = false }) { Text("Cancel") } }
            )
        }

        selectedPlayerForAction?.let { player ->
            AlertDialog(
                onDismissRequest = { selectedPlayerForAction = null },
                title = { Text("Manage ${player.name}") },
                text = {
                    Column {
                        TextButton(onClick = {
                            viewModel.assignCaptain(teamId, player.playerId)
                            selectedPlayerForAction = null
                        }) { Text("Assign as Captain (C)") }

                        TextButton(onClick = {
                            viewModel.assignViceCaptain(teamId, player.playerId)
                            selectedPlayerForAction = null
                        }) { Text("Assign as Vice-Captain (VC)") }

                        TextButton(onClick = {
                            viewModel.removePlayerFromTeam(teamId, player.playerId)
                            selectedPlayerForAction = null
                        }) { Text("Remove from Squad", color = MaterialTheme.colorScheme.error) }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { selectedPlayerForAction = null }) { Text("Close") } }
            )
        }
    }
}
