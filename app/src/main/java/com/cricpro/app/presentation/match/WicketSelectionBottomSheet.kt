package com.cricpro.app.presentation.match

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cricpro.app.R
import com.cricpro.app.domain.model.WicketType
import com.cricpro.app.presentation.theme.CricketGreenPrimary

data class WicketItem(
    val type: WicketType,
    val title: String,
    @DrawableRes val iconRes: Int
)

val wicketItemsList = listOf(
    WicketItem(WicketType.BOWLED, "BOWLED", R.drawable.ic_wicket_bowled),
    WicketItem(WicketType.CAUGHT, "CAUGHT", R.drawable.ic_wicket_caught),
    WicketItem(WicketType.LBW, "LBW", R.drawable.ic_wicket_lbw),
    WicketItem(WicketType.RUN_OUT, "RUN OUT", R.drawable.ic_wicket_run_out),
    WicketItem(WicketType.STUMPED, "STUMPED", R.drawable.ic_wicket_stumped),
    WicketItem(WicketType.HIT_WICKET, "HIT WICKET", R.drawable.ic_wicket_hit_wicket),
    WicketItem(WicketType.RETIRED_OUT, "RETIRED OUT", R.drawable.ic_wicket_retired_out)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WicketSelectionBottomSheet(
    onSelectWicket: (WicketType) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .background(Color(0xFFD1D5DB), RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Text(
                text = "Select Wicket Type",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
            ) {
                items(wicketItemsList, key = { it.type.name }) { item ->
                    WicketItemCard(
                        item = item,
                        onClick = { onSelectWicket(item.type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, CricketGreenPrimary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CricketGreenPrimary)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricketGreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun WicketItemCard(
    item: WicketItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFFFF),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Badge Circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFFEE2E2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = Color(0xFF0F172A),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
