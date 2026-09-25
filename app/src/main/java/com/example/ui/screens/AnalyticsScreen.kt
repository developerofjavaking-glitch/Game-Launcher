package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GameSessionEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.theme.BeastRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    sessions: List<GameSessionEntity>,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPlayTimeMillis = remember(sessions) { sessions.sumOf { it.durationMillis } }
    val totalHours = totalPlayTimeMillis / (1000 * 60 * 60)
    val totalMins = (totalPlayTimeMillis / (1000 * 60)) % 60
    val avgFpsAll = remember(sessions) {
        if (sessions.isNotEmpty()) sessions.map { it.avgFps }.average().toFloat() else 60.0f
    }

    // Most played games distribution
    val gamesDistribution = remember(sessions) {
        sessions.groupBy { it.gameTitle }
            .mapValues { entry -> entry.value.sumOf { it.durationMillis } }
            .toList()
            .sortedByDescending { it.second }
            .take(4)
    }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.US) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "GAMING STATS & SESSION LOGS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Historical FPS Stability, Playtime & Battery Records",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Overview Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Play Time
                CyberCard(
                    modifier = Modifier.weight(1f),
                    borderColor = CyberCyan
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Timelapse, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                        Text(
                            text = "${totalHours}h ${totalMins}m",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(text = "Total Playtime", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Average FPS
                CyberCard(
                    modifier = Modifier.weight(1f),
                    borderColor = NeonGreen
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                        Text(
                            text = String.format(Locale.US, "%.1f", avgFpsAll),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(text = "Average FPS", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                // Sessions Count
                CyberCard(
                    modifier = Modifier.weight(1f),
                    borderColor = CyberPurple
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Assessment, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(20.dp))
                        Text(
                            text = "${sessions.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(text = "Sessions Logged", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Most Played Games Breakdown
        if (gamesDistribution.isNotEmpty()) {
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "MOST PLAYED TITLES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )

                        gamesDistribution.forEach { (title, duration) ->
                            val mins = (duration / (1000 * 60)).toInt()
                            val pct = if (totalPlayTimeMillis > 0) (duration.toFloat() / totalPlayTimeMillis.toFloat()) else 0f

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = title, fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text(text = "${mins}m (${(pct * 100).toInt()}%)", fontSize = 11.sp, color = CyberCyan)
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(CyberSurfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct.coerceIn(0.05f, 1f))
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Brush.horizontalGradient(listOf(CyberCyan, CyberPurple)))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Past Sessions List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Text(
                        text = "SESSION HISTORY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                if (sessions.isNotEmpty()) {
                    Button(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberSurfaceVariant,
                            contentColor = BeastRed
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(14.dp), tint = BeastRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLEAR", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (sessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "No sessions recorded yet",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Launch any game from the library to begin logging FPS, thermals, and playtime telemetry.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                val durationSec = session.durationMillis / 1000
                val mins = durationSec / 60
                val secs = durationSec % 60
                val durationText = if (mins > 0) "${mins}m ${secs}s" else "${secs}s"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                        .testTag("session_item_${session.id}")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = session.gameTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            CyberBadge(
                                text = session.performanceMode,
                                color = if (session.performanceMode.contains("Beast", ignoreCase = true)) BeastRed else CyberCyan
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                                    Text(
                                        text = String.format(Locale.US, "%.1f FPS", session.avgFps),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.Timelapse, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                    Text(text = durationText, fontSize = 12.sp, color = TextSecondary)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(imageVector = Icons.Default.Thermostat, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(14.dp))
                                    Text(text = String.format(Locale.US, "%.1f°C", session.peakTempCelsius), fontSize = 12.sp, color = TextSecondary)
                                }
                            }

                            Text(
                                text = dateFormatter.format(Date(session.startTime)),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
