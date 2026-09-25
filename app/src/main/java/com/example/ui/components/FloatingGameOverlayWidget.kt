package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppGameSettings
import com.example.data.model.HardwareTelemetry
import com.example.data.model.RecordingStatusState
import com.example.ui.theme.BeastRed
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
import kotlin.math.roundToInt

@Composable
fun FloatingGameOverlayWidget(
    telemetry: HardwareTelemetry,
    recordingState: RecordingStatusState,
    settings: AppGameSettings,
    gameTitle: String,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onCaptureScreenshot: () -> Unit,
    onQuickBoost: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleCrosshair: () -> Unit,
    onToggleDnd: () -> Unit,
    onCloseOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(20f) }
    var offsetY by remember { mutableFloatStateOf(160f) }
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "recPulse")
    val recAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recAlpha"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .testTag("floating_game_overlay_widget")
    ) {
        if (!isExpanded) {
            // Minimized Floating Bubble / Pill (XRecorder style)
            Row(
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xE6080B11), Color(0xE6141D2B))
                        )
                    )
                    .border(
                        1.5.dp,
                        if (recordingState.isRecording) BeastRed else CyberCyan.copy(alpha = 0.7f),
                        RoundedCornerShape(24.dp)
                    )
                    .clickable { isExpanded = true }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("floating_pill_minimized"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag overlay",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )

                if (recordingState.isRecording) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(recAlpha)
                            .clip(CircleShape)
                            .background(BeastRed)
                    )
                    Text(
                        text = "REC ${recordingState.formattedTime}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BeastRed
                    )
                } else {
                    // Realtime FPS
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.0f", telemetry.liveFps),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NeonGreen
                        )
                        Text(
                            text = "FPS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(14.dp)
                            .background(CyberBorder)
                    )

                    // Realtime Temperature
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.0f°C", telemetry.batteryTemperatureCelsius),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GoldYellow
                        )
                    }
                }

                // Quick tap hint
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (recordingState.isRecording) Icons.Default.Stop else Icons.Default.Videocam,
                        contentDescription = "Expand controls",
                        tint = if (recordingState.isRecording) BeastRed else CyberCyan,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        } else {
            // Expanded Floating Action Window (XRecorder / Samsung Game Hub style)
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp, max = 340.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xF50D111A))
                    .border(1.5.dp, CyberCyan, RoundedCornerShape(20.dp))
                    .padding(14.dp)
                    .testTag("floating_pill_expanded"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (recordingState.isRecording) BeastRed else CyberCyan)
                        )
                        Text(
                            text = "JAIRO ASSISTANT",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberSurfaceVariant)
                                .clickable { isExpanded = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Minimize", fontSize = 10.sp, color = TextSecondary)
                        }

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close overlay",
                            tint = TextMuted,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    isExpanded = false
                                    onCloseOverlay()
                                }
                        )
                    }
                }

                // Telemetry Row: FPS, Temp, RAM, Ping
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelemetryStatItem(
                        value = String.format(Locale.US, "%.0f", telemetry.liveFps),
                        unit = "FPS",
                        color = NeonGreen
                    )
                    TelemetryStatItem(
                        value = String.format(Locale.US, "%.1f°C", telemetry.batteryTemperatureCelsius),
                        unit = "TEMP",
                        color = GoldYellow
                    )
                    TelemetryStatItem(
                        value = "${telemetry.ramUsedPercent}%",
                        unit = "RAM",
                        color = CyberCyan
                    )
                    TelemetryStatItem(
                        value = "${telemetry.pingMs}ms",
                        unit = "PING",
                        color = if (telemetry.pingMs < 50) NeonGreen else BeastRed
                    )
                }

                // Main Action: Video Recording Bar
                if (recordingState.isRecording) {
                    // Recording Active State
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BeastRed.copy(alpha = 0.2f))
                            .border(1.5.dp, BeastRed, RoundedCornerShape(14.dp))
                            .clickable { onStopRecord() }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .alpha(recAlpha)
                                    .clip(CircleShape)
                                    .background(BeastRed)
                            )
                            Column {
                                Text(
                                    text = "RECORDING GAMEPLAY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BeastRed
                                )
                                Text(
                                    text = "${recordingState.formattedTime} • ${settings.resolution.badge} @ ${settings.frameRate.label}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BeastRed)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = "Stop",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "STOP",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    // Idle Record Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CyberCyan.copy(alpha = 0.2f), CyberPurple.copy(alpha = 0.2f))
                                )
                            )
                            .border(1.dp, CyberCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .clickable { onStartRecord() }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(BeastRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FiberManualRecord,
                                    contentDescription = "Record",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Record Game Video",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${settings.resolution.label} • Saves to Gallery",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        CyberBadge(text = settings.resolution.badge, color = CyberCyan)
                    }
                }

                // In-Game Quick Action Icons (Screenshot, Boost, Crosshair, DND, Settings)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FloatingActionButton(
                        icon = Icons.Default.CameraAlt,
                        label = "Shot",
                        color = CyberCyan,
                        onClick = onCaptureScreenshot
                    )

                    FloatingActionButton(
                        icon = Icons.Default.Bolt,
                        label = "Boost",
                        color = GoldYellow,
                        onClick = onQuickBoost
                    )

                    FloatingActionButton(
                        icon = Icons.Default.CenterFocusStrong,
                        label = "Crosshair",
                        color = CyberPurple,
                        onClick = onToggleCrosshair
                    )

                    FloatingActionButton(
                        icon = Icons.Default.NotificationsOff,
                        label = "DND",
                        color = BeastRed,
                        onClick = onToggleDnd
                    )

                    FloatingActionButton(
                        icon = Icons.Default.Settings,
                        label = "Settings",
                        color = TextPrimary,
                        onClick = {
                            isExpanded = false
                            onOpenSettings()
                        }
                    )
                }

                // Footer note
                Text(
                    text = "Videos & screenshots saved directly to Gallery / Movies",
                    fontSize = 10.sp,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun TelemetryStatItem(
    value: String,
    unit: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = color
        )
        Text(
            text = unit,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
    }
}

@Composable
private fun FloatingActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CyberSurfaceCard)
                .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
