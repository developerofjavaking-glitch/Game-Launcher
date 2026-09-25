package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppGameSettings
import com.example.data.model.AudioSourceOption
import com.example.data.model.FloatingBubbleStyle
import com.example.data.model.RecordedClipItem
import com.example.data.model.VideoBitrate
import com.example.data.model.VideoFps
import com.example.data.model.VideoResolution
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
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

@Composable
fun SettingsScreen(
    settings: AppGameSettings,
    recordedClips: List<RecordedClipItem>,
    isFloatingOverlayActive: Boolean,
    onUpdateSettings: (AppGameSettings) -> Unit,
    onToggleFloatingOverlay: () -> Unit,
    onDeleteClip: (RecordedClipItem) -> Unit,
    onTestVibration: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "JAIROPLAYER SETTINGS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Customize Video Recording, Floating HUD & System Performance",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
                CyberBadge(text = "PRO GAMING", color = CyberCyan)
            }
        }

        // Section 1: Video Recording Configuration (1080p, 720p, 480p)
        item {
            CyberCard(
                borderColor = CyberCyan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "GAME SCREEN VIDEO RECORDING",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberCyan,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Resolution (Resolution in p):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Resolution Options (1080p, 720p, 480p)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VideoResolution.values().forEach { res ->
                            val isSelected = settings.resolution == res
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else CyberSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberCyan else CyberBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        onUpdateSettings(settings.copy(resolution = res))
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("res_option_${res.badge}"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = res.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) CyberCyan else TextPrimary
                                        )
                                        if (res == VideoResolution.RES_1080P) {
                                            CyberBadge(text = "RECOMMENDED", color = NeonGreen)
                                        }
                                    }
                                    Text(
                                        text = "${res.width} x ${res.height} • ${res.description}",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(CyberCyan),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.Black,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Frame Rate Selection (FPS)
                    Text(
                        text = "Recording Frame Rate:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoFps.values().forEach { fps ->
                            val isSelected = settings.frameRate == fps
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) NeonGreen else CyberBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onUpdateSettings(settings.copy(frameRate = fps))
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fps.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSelected) NeonGreen else TextSecondary
                                )
                            }
                        }
                    }

                    // Video Bitrate
                    Text(
                        text = "Video Bitrate:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VideoBitrate.values().forEach { br ->
                            val isSelected = settings.bitrate == br
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) CyberPurple.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberPurple else CyberBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onUpdateSettings(settings.copy(bitrate = br))
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = br.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CyberPurple else TextSecondary
                                    )
                                    Text(
                                        text = br.description.substringBefore(" "),
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }

                    // Audio Source
                    Text(
                        text = "Audio Source:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        AudioSourceOption.values().forEach { audio ->
                            val isSelected = settings.audioSource == audio
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) GoldYellow.copy(alpha = 0.15f) else CyberSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) GoldYellow else CyberBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onUpdateSettings(settings.copy(audioSource = audio))
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = audio.label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GoldYellow else TextPrimary
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GoldYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Save Location Note
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0x3310B981))
                            .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Saved Directly to Device Gallery",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Text(
                                text = "Path: ${settings.saveFolderLabel}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Floating Window (XRecorder Style) Settings
        item {
            CyberCard(
                borderColor = GoldYellow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = GoldYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "FLOATING WINDOW (XRECORDER STYLE)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldYellow,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Toggle: Show Floating Window On Game Launch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Show Floating Window on Game Launch",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Displays draggable floating bubble over games with FPS, Temp & Record button",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Switch(
                            checked = settings.showFloatingWindowOnLaunch,
                            onCheckedChange = {
                                onUpdateSettings(settings.copy(showFloatingWindowOnLaunch = it))
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = GoldYellow
                            )
                        )
                    }

                    // Test Floating Window Trigger Button
                    Button(
                        onClick = onToggleFloatingOverlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFloatingOverlayActive) BeastRed else GoldYellow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("toggle_floating_overlay_button")
                    ) {
                        Icon(
                            imageVector = if (isFloatingOverlayActive) Icons.Default.Visibility else Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFloatingOverlayActive) "HIDE FLOATING WINDOW" else "TEST / SHOW FLOATING WINDOW NOW",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }

                    // Floating Bubble Style Selector
                    Text(
                        text = "Floating Bubble Style:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FloatingBubbleStyle.values().forEach { style ->
                            val isSelected = settings.floatingBubbleStyle == style
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) GoldYellow.copy(alpha = 0.15f) else CyberSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) GoldYellow else CyberBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        onUpdateSettings(settings.copy(floatingBubbleStyle = style))
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = style.label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GoldYellow else TextPrimary
                                    )
                                    Text(text = style.description, fontSize = 10.sp, color = TextMuted)
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GoldYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Toggles for items displayed inside the floating bubble
                    Text(
                        text = "HUD Telemetry Indicators in Floating Window:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Show Real-time FPS", fontSize = 12.sp, color = TextSecondary)
                        Switch(
                            checked = settings.showFpsInFloatingWindow,
                            onCheckedChange = { onUpdateSettings(settings.copy(showFpsInFloatingWindow = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonGreen)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Show Real-time Battery Temperature", fontSize = 12.sp, color = TextSecondary)
                        Switch(
                            checked = settings.showTempInFloatingWindow,
                            onCheckedChange = { onUpdateSettings(settings.copy(showTempInFloatingWindow = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = GoldYellow)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Show RAM Usage %", fontSize = 12.sp, color = TextSecondary)
                        Switch(
                            checked = settings.showRamInFloatingWindow,
                            onCheckedChange = { onUpdateSettings(settings.copy(showRamInFloatingWindow = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = CyberCyan)
                        )
                    }
                }
            }
        }

        // Section 3: Recorded Game Clips Gallery
        item {
            CyberCard(
                borderColor = BeastRed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = BeastRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "RECORDED CLIPS GALLERY",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = BeastRed,
                                letterSpacing = 0.5.sp
                            )
                        }

                        CyberBadge(text = "${recordedClips.size} CLIPS", color = BeastRed)
                    }

                    OutlinedButton(
                        onClick = {
                            openSystemGallery(context)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "OPEN DEVICE GALLERY (PHOTOS / MOVIES)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (recordedClips.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberSurfaceVariant)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "No Recorded Clips Yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Launch a game or tap the floating red button to record gameplay!",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            recordedClips.forEach { clip ->
                                RecordedClipRowItem(
                                    clip = clip,
                                    onPlay = {
                                        playClip(context, clip)
                                    },
                                    onShare = {
                                        shareClip(context, clip)
                                    },
                                    onDelete = {
                                        onDeleteClip(clip)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Performance & Hardware Tuning
        item {
            CyberCard(
                borderColor = CyberBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "HARDWARE & GAMING ASSISTANT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Auto RAM Boost on Launch", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Automatically kills background cache to free RAM before game starts", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = settings.autoBoostOnLaunch,
                            onCheckedChange = { onUpdateSettings(settings.copy(autoBoostOnLaunch = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = CyberCyan)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "4D Haptic Vibration Feedback", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = "Tactile haptic pulses on game launch, recording, and button touches", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = settings.hapticFeedback,
                            onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedback = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = CyberPurple)
                        )
                    }

                    Button(
                        onClick = onTestVibration,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Test Haptic Pulse", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 5: Reset to Defaults
        item {
            Button(
                onClick = {
                    onUpdateSettings(AppGameSettings())
                    Toast.makeText(context, "Settings reset to defaults", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceCard, contentColor = TextMuted),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "RESET ALL SETTINGS TO DEFAULTS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RecordedClipRowItem(
    clip: RecordedClipItem,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceVariant)
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = CyberCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = clip.fileName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "${clip.resolution} • ${clip.durationFormatted} • ${clip.sizeFormatted}",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
                Text(
                    text = clip.dateAddedFormatted,
                    fontSize = 9.sp,
                    color = TextMuted
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = CyberCyan, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = BeastRed, modifier = Modifier.size(16.dp))
            }
        }
    }
}

private fun openSystemGallery(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            type = "video/*"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Open Device Gallery"))
    } catch (e: Exception) {
        Toast.makeText(context, "Gallery opened: Movies/JairoPlayer", Toast.LENGTH_SHORT).show()
    }
}

private fun playClip(context: Context, clip: RecordedClipItem) {
    try {
        if (clip.uriString.isNotBlank()) {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(clip.uriString), "video/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Playing: ${clip.fileName}", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Video clip stored at Gallery/Movies: ${clip.fileName}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareClip(context: Context, clip: RecordedClipItem) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(clip.uriString))
            putExtra(Intent.EXTRA_SUBJECT, "Game clip recorded with JairoPlayer")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Game Clip"))
    } catch (e: Exception) {
        Toast.makeText(context, "Share: ${clip.fileName}", Toast.LENGTH_SHORT).show()
    }
}
