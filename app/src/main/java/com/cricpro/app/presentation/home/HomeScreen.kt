package com.cricpro.app.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.cricpro.app.domain.model.Match
import com.cricpro.app.domain.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreateMatch: () -> Unit,
    onNavigateToTeams: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onMatchClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedBottomItem by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CricPro", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedBottomItem == 0,
                    onClick = { selectedBottomItem = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, maxLines = 1) },
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    selected = selectedBottomItem == 1,
                    onClick = {
                        selectedBottomItem = 1
                        onNavigateToTeams()
                    },
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Teams") },
                    label = { Text("Teams", fontSize = 11.sp, maxLines = 1) },
                    alwaysShowLabel = true
                )
                NavigationBarItem(
                    selected = selectedBottomItem == 2,
                    onClick = {
                        selectedBottomItem = 2
                        onNavigateToCreateMatch()
                    },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "New Match") },
                    label = { Text("New Match", fontSize = 10.sp, maxLines = 1) },
                    alwaysShowLabel = true
                )
                if (uiState.isTournamentTabEnabled) {
                    NavigationBarItem(
                        selected = selectedBottomItem == 3,
                        onClick = {
                            selectedBottomItem = 3
                            onNavigateToTournaments()
                        },
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Tournaments") },
                        label = { Text("Tournaments", fontSize = 10.sp, maxLines = 1) },
                        alwaysShowLabel = true
                    )
                }
                NavigationBarItem(
                    selected = selectedBottomItem == 4,
                    onClick = {
                        selectedBottomItem = 4
                        onNavigateToSettings()
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, maxLines = 1) },
                    alwaysShowLabel = true
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Recent Matches Section
            item {
                Text(
                    text = "Recent Matches",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.recentMatches.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "No recent matches found. Tap 'New Match' to start!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.recentMatches, key = { it.matchId }) { match ->
                            MatchCard(match = match, onClick = { onMatchClick(match.matchId) })
                        }
                    }
                }
            }

            // Completed Matches Section
            item {
                Text(
                    text = "Completed Matches 🏆",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (uiState.completedMatches.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "No completed matches yet.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.completedMatches, key = { it.matchId }) { match ->
                            MatchCard(match = match, onClick = { onMatchClick(match.matchId) })
                        }
                    }
                }
            }

            // My Teams Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Teams",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToTeams) {
                        Text("View All")
                    }
                }
                if (uiState.teams.isEmpty()) {
                    Text(
                        text = "No teams created yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.teams) { team ->
                            TeamChipCard(team = team)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun MatchCard(match: Match, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(260.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.matchType.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when (match.status) {
                        com.cricpro.app.domain.model.MatchStatus.COMPLETED -> "COMPLETED"
                        com.cricpro.app.domain.model.MatchStatus.IN_PROGRESS -> "LIVE"
                        else -> "SCHEDULED"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(match.teamA.teamName, fontWeight = FontWeight.Bold)
                Text("${match.firstInnings?.totalRuns ?: 0}/${match.firstInnings?.wickets ?: 0}")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(match.teamB.teamName, fontWeight = FontWeight.Bold)
                Text("${match.secondInnings?.totalRuns ?: 0}/${match.secondInnings?.wickets ?: 0}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = match.resultMessage.ifEmpty { match.status.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TeamChipCard(team: Team) {
    Card {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(team.teamName, fontWeight = FontWeight.Bold)
                Text("${team.players.size} Players", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
