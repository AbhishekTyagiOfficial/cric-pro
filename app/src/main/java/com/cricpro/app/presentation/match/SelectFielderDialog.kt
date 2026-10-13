package com.cricpro.app.presentation.match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cricpro.app.domain.model.Player
import com.cricpro.app.domain.model.WicketType
import com.cricpro.app.presentation.theme.CricketGreenPrimary

@Composable
fun SelectFielderDialog(
    fieldingTeamName: String,
    fieldingPlayers: List<Player>,
    wicketType: WicketType = WicketType.CAUGHT,
    onFielderSelected: (fielderId: String, fielderName: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Gracefully fallback to 11 generic fielders if fielding team roster is missing/empty
    val eligiblePlayers = remember(fieldingPlayers, fieldingTeamName) {
        if (fieldingPlayers.isNotEmpty()) {
            fieldingPlayers
        } else {
            (1..11).map { idx ->
                Player(
                    playerId = "fielder_$idx",
                    name = if (fieldingTeamName.isNotBlank()) "$fieldingTeamName Fielder $idx" else "Fielder $idx"
                )
            }
        }
    }

    val subtitleAction = when (wicketType) {
        WicketType.CAUGHT -> "caught the ball"
        WicketType.RUN_OUT -> "executed the run out"
        else -> "fielded the ball"
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) onDismiss()
        },
        title = {
            Column {
                Text(
                    text = "Select Fielder",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1D1B20)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (fieldingTeamName.isNotBlank()) {
                        "Select player from $fieldingTeamName who $subtitleAction"
                    } else {
                        "Select fielding player who $subtitleAction"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(eligiblePlayers, key = { it.playerId }) { player ->
                        val isSelected = selectedPlayer?.playerId == player.playerId
                        Surface(
                            onClick = {
                                if (!isSubmitting) {
                                    selectedPlayer = player
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF8F9FA),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CricketGreenPrimary else Color(0xFFE0E0E0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CricketGreenPrimary else Color(0xFFECEFF1)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFF546E7A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = player.name.ifBlank { player.playerId },
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF212121)
                                    )
                                    if (player.playerId.isNotBlank() && player.playerId != player.name) {
                                        Text(
                                            text = "ID: ${player.playerId}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF757575)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                                    contentDescription = if (isSelected) "Selected" else "Not selected",
                                    tint = if (isSelected) CricketGreenPrimary else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(22.dp)
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
                    if (selectedPlayer != null && !isSubmitting) {
                        isSubmitting = true
                        val player = selectedPlayer!!
                        onFielderSelected(player.playerId, player.name.ifBlank { player.playerId })
                    }
                },
                enabled = selectedPlayer != null && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CricketGreenPrimary,
                    disabledContainerColor = Color(0xFFCCCCCC)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Confirm",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    if (!isSubmitting) {
                        onDismiss()
                    }
                },
                enabled = !isSubmitting,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Cancel",
                    color = Color(0xFF424242)
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}
