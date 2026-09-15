package com.cricpro.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.cricpro.app.domain.model.Ball
import com.cricpro.app.domain.model.Innings
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WagonWheelCanvas(
    balls: List<Ball>,
    modifier: Modifier = Modifier.size(280.dp)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2.2f

            // Draw Outfield boundary circle (Green)
            drawCircle(
                color = Color(0xFF2E7D32),
                radius = radius,
                center = center
            )
            drawCircle(
                color = Color.White,
                radius = radius,
                center = center,
                style = Stroke(width = 4f)
            )

            // Draw Pitch (Rectangular box in middle)
            val pitchWidth = 24f
            val pitchHeight = 60f
            drawRect(
                color = Color(0xFFD7CCC8),
                topLeft = Offset(center.x - pitchWidth / 2, center.y - pitchHeight / 2),
                size = androidx.compose.ui.geometry.Size(pitchWidth, pitchHeight)
            )

            // Draw shot lines from pitch center to boundary/distance fraction
            for (ball in balls) {
                val wagon = ball.wagonWheel ?: continue
                val radians = Math.toRadians(wagon.angleDegrees.toDouble())
                val shotLength = radius * wagon.distanceFraction.coerceIn(0.1f, 1.0f)

                val endX = center.x + (shotLength * cos(radians)).toFloat()
                val endY = center.y + (shotLength * sin(radians)).toFloat()

                val lineStyleColor = when (ball.runsScored) {
                    6 -> Color(0xFFE53935) // Red for 6s
                    4 -> Color(0xFFFB8C00) // Orange for 4s
                    3 -> Color(0xFFFDD835) // Yellow
                    2 -> Color(0xFF00ACC1) // Cyan
                    1 -> Color(0xFF43A047) // Light Green
                    else -> Color.Gray
                }

                drawLine(
                    color = lineStyleColor,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = if (ball.runsScored >= 4) 5f else 3f
                )
            }
        }
    }
}

@Composable
fun ManhattanChart(
    innings: Innings,
    modifier: Modifier = Modifier.fillMaxWidth().height(220.dp)
) {
    val oversMap = mutableMapOf<Int, Int>()
    for (ball in innings.ballsHistory) {
        val overIdx = ball.overNumber + 1
        oversMap[overIdx] = (oversMap[overIdx] ?: 0) + ball.totalRunsOnBall
    }

    val maxRuns = (oversMap.values.maxOrNull() ?: 10).coerceAtLeast(6)

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val numOvers = oversMap.keys.maxOrNull() ?: 20
            val barWidth = (width / numOvers) * 0.7f

            for (i in 1..numOvers) {
                val runs = oversMap[i] ?: 0
                val barHeight = (runs.toFloat() / maxRuns) * (height - 40f)
                val x = (i - 1) * (width / numOvers) + (barWidth / 2)
                val y = height - barHeight - 20f

                drawRect(
                    color = Color(0xFF1E88E5),
                    topLeft = Offset(x, y),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                )
            }
        }
    }
}

@Composable
fun WormChart(
    inn1: Innings?,
    inn2: Innings?,
    modifier: Modifier = Modifier.fillMaxWidth().height(220.dp)
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        fun drawInningsLine(inn: Innings, color: Color) {
            val path = Path()
            var cumulativeRuns = 0
            val balls = inn.ballsHistory

            if (balls.isEmpty()) return
            path.moveTo(0f, height)

            for ((idx, ball) in balls.withIndex()) {
                cumulativeRuns += ball.totalRunsOnBall
                val x = (idx.toFloat() / (balls.size.coerceAtLeast(1))) * width
                val y = height - ((cumulativeRuns.toFloat() / 250f) * height).coerceAtMost(height)
                path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(width = 4f)
            )
        }

        if (inn1 != null) drawInningsLine(inn1, Color(0xFF43A047))
        if (inn2 != null) drawInningsLine(inn2, Color(0xFFE53935))
    }
}
