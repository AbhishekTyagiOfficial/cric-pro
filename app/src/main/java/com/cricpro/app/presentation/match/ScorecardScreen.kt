package com.cricpro.app.presentation.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricpro.app.utils.ExcelExporter
import com.cricpro.app.utils.PdfExporter

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Full Scorecard", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text((match?.title ?: "").ifEmpty { "Match Scorecard" }, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text((match?.resultMessage ?: "").ifEmpty { "In Progress" }, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }

            // 1st Innings Section
            item {
                val inn1 = match?.firstInnings
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1st Innings - ${inn1?.totalRuns ?: 0}/${inn1?.wickets ?: 0} (${inn1?.oversFormatted ?: "0.0"} overs)", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Batting Table", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        inn1?.batters?.values?.forEach { batter ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(batter.name.ifEmpty { batter.playerId }, modifier = Modifier.weight(1f))
                                Text("${batter.runs} (${batter.balls}b, ${batter.fours}x4, ${batter.sixes}x6)")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Extras: ${inn1?.extras?.total ?: 0} (wd ${inn1?.extras?.wides}, nb ${inn1?.extras?.noBalls}, b ${inn1?.extras?.byes}, lb ${inn1?.extras?.legByes})", fontSize = 12.sp)
                    }
                }
            }

            // 2nd Innings Section
            item {
                val inn2 = match?.secondInnings
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("2nd Innings - ${inn2?.totalRuns ?: 0}/${inn2?.wickets ?: 0} (${inn2?.oversFormatted ?: "0.0"} overs)", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Batting Table", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        inn2?.batters?.values?.forEach { batter ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(batter.name.ifEmpty { batter.playerId }, modifier = Modifier.weight(1f))
                                Text("${batter.runs} (${batter.balls}b, ${batter.fours}x4, ${batter.sixes}x6)")
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Extras: ${inn2?.extras?.total ?: 0} (wd ${inn2?.extras?.wides}, nb ${inn2?.extras?.noBalls}, b ${inn2?.extras?.byes}, lb ${inn2?.extras?.legByes})", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
