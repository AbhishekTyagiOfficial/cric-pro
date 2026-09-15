package com.cricpro.app.presentation.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.presentation.components.ManhattanChart
import com.cricpro.app.presentation.components.WagonWheelCanvas
import com.cricpro.app.presentation.components.WormChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchAnalyticsScreen(
    matchId: String,
    onNavigateBack: () -> Unit,
    viewModel: ScoringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val match = uiState.currentMatch
    val currentInnings = if (match?.currentInningsNumber == 1) match.firstInnings else match?.secondInnings

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Wagon Wheel Shot Distribution", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    WagonWheelCanvas(balls = currentInnings?.ballsHistory ?: emptyList())
                }
            }

            Text("Manhattan Over-by-Over Graph", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (currentInnings != null) {
                        ManhattanChart(innings = currentInnings)
                    }
                }
            }

            Text("Worm Run Comparison Graph", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WormChart(inn1 = match?.firstInnings, inn2 = match?.secondInnings)
                }
            }
        }
    }
}
