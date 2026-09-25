package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.db.GameSessionEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.HardwareTelemetry
import com.example.ui.components.CrosshairRenderer
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
import java.util.Locale

@Composable
fun ActiveGameSessionDialog(
    sessionState: ActiveSessionState,
    telemetry: HardwareTelemetry,
    onEndSession: () -> Unit,
    onToggleDnd: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleCrosshair: (Boolean) -> Unit,
    onToggleMistouch: (Boolean) -> Unit
) {
    if (!sessionState.isActive || sessionState.game == null) return

    val game = sessionState.game
    val durationSec = sessionState.currentDurationMillis / 1000
    val mins = durationSec / 60
    val secs = durationSec % 60
    val timerString = String.format(Locale.US, "%02d:%02d", mins, secs)

    Dialog(
        onDismissRequest = onEndSession,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF06080D))
                .testTag("active_game_session_overlay")
        ) {
            // Background Artwork
            Image(
                painter = painterResource(id = R.drawable.img_game_hero),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.45f
            )

            // Crosshair overlay if enabled
            if (sessionState.isCrosshairEnabled) {
                CrosshairRenderer(
                    styleIndex = sessionState.crosshairStyleIndex,
                    color = CyberCyan,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                )
            }

            // Top Floating HUD (Samsung Game Booster / ROG Game Genie In-Game Pill)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xE60D121D))
                        .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Game & Timer
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = game.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            CyberBadge(text = sessionState.performanceMode.title, color = BeastRed)
                        }
                        Text(
                            text = "SESSION TIME: $timerString",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }

                    // Live Telemetry Pill
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format(Locale.US, "%.0f", sessionState.currentFps),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = NeonGreen
                            )
                            Text(text = "FPS", fontSize = 9.sp, color = TextMuted)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${telemetry.pingMs}ms",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (telemetry.pingMs < 45) NeonGreen else BeastRed
                            )
                            Text(text = "PING", fontSize = 9.sp, color = TextMuted)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = String.format(Locale.US, "%.1f°C", sessionState.peakTemp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldYellow
                            )
                            Text(text = "TEMP", fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Bottom In-Game Toolkit Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Floating Toolkit Icons Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xF00D121D))
                        .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // DND Toggle
                    InGameToolIconButton(
                        icon = Icons.Default.NotificationsOff,
                        label = "DND",
                        isActive = sessionState.isDndActive,
                        activeColor = BeastRed,
                        onClick = { onToggleDnd(!sessionState.isDndActive) }
                    )

                    // 4D Haptics
                    InGameToolIconButton(
                        icon = Icons.Default.Vibration,
                        label = "4D Touch",
                        isActive = sessionState.is4DHapticsActive,
                        activeColor = CyberPurple,
                        onClick = { onToggleHaptics(!sessionState.is4DHapticsActive) }
                    )

                    // Crosshair Toggle
                    InGameToolIconButton(
                        icon = Icons.Default.CenterFocusStrong,
                        label = "Crosshair",
                        isActive = sessionState.isCrosshairEnabled,
                        activeColor = CyberCyan,
                        onClick = { onToggleCrosshair(!sessionState.isCrosshairEnabled) }
                    )

                    // Mistouch Lock
                    InGameToolIconButton(
                        icon = Icons.Default.TouchApp,
                        label = "Mistouch",
                        isActive = sessionState.isMistouchLockActive,
                        activeColor = GoldYellow,
                        onClick = { onToggleMistouch(!sessionState.isMistouchLockActive) }
                    )
                }

                // End Session Button
                Button(
                    onClick = onEndSession,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BeastRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("end_game_session_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EXIT & SAVE SESSION STATS",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun InGameToolIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor.copy(alpha = 0.2f) else CyberSurfaceVariant)
                .border(1.dp, if (isActive) activeColor else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeColor else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isActive) TextPrimary else TextMuted,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}
