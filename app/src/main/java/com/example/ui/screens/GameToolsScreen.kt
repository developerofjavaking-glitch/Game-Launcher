package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun GameToolsScreen(
    onTestHaptic: () -> Unit,
    modifier: Modifier = Modifier
) {
    var is4DHapticEnabled by remember { mutableStateOf(true) }
    var isDndEnabled by remember { mutableStateOf(true) }
    var isMistouchLocked by remember { mutableStateOf(false) }
    var isCrosshairEnabled by remember { mutableStateOf(false) }
    var crosshairStyle by remember { mutableStateOf(1) } // 0: dot, 1: cross, 2: circle dot, 3: diamond
    var crosshairColorIndex by remember { mutableStateOf(0) }
    var targetRefreshRate by remember { mutableStateOf(120) }

    val crosshairColors = listOf(CyberCyan, BeastRed, NeonGreen, GoldYellow)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
            .testTag("game_tools_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "GAMING ASSISTANT & TOOLS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ROG Game Genie & Samsung Game Booster Toolset",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Custom Tactical Crosshair Generator
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberCyan,
                cornerRadius = 18.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "TACTICAL CROSSHAIR OVERLAY",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Switch(
                            checked = isCrosshairEnabled,
                            onCheckedChange = { isCrosshairEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberCyan,
                                checkedTrackColor = CyberCyan.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Text(
                        text = "Static zero-bloom crosshair for competitive first-person shooters (PUBG, COD Mobile, Free Fire).",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    // Crosshair Preview Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF07090E))
                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CrosshairRenderer(
                            styleIndex = crosshairStyle,
                            color = crosshairColors[crosshairColorIndex],
                            modifier = Modifier.size(90.dp)
                        )
                        Text(
                            text = if (isCrosshairEnabled) "OVERLAY ACTIVE" else "PREVIEW ONLY",
                            fontSize = 9.sp,
                            color = if (isCrosshairEnabled) NeonGreen else TextMuted,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        )
                    }

                    // Style selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val styles = listOf("Dot", "Cross", "Circle", "Diamond")
                        styles.forEachIndexed { index, styleName ->
                            val isSelected = crosshairStyle == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) CyberCyan else CyberBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { crosshairStyle = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = styleName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyberCyan else TextSecondary
                                )
                            }
                        }
                    }

                    // Color picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Color Tone:", fontSize = 11.sp, color = TextMuted)
                        crosshairColors.forEachIndexed { index, col ->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(col)
                                    .border(
                                        2.dp,
                                        if (crosshairColorIndex == index) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { crosshairColorIndex = index }
                            )
                        }
                    }
                }
            }
        }

        // 4D Haptic Feedback Tool
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberBorder,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = CyberPurple,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "4D TACTILE HAPTIC FEEDBACK",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Switch(
                            checked = is4DHapticEnabled,
                            onCheckedChange = { is4DHapticEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberPurple,
                                checkedTrackColor = CyberPurple.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Text(
                        text = "Translates in-game explosions, gunfire recoil, and vehicle revs into precision dual-axis vibration pulses.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Button(
                        onClick = onTestHaptic,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberSurfaceVariant,
                            contentColor = CyberPurple
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TEST 4D VIBRATION PULSE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Do Not Disturb & Gesture Lock Tools
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberBorder,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // DND Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsOff, contentDescription = null, tint = BeastRed, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Block Heads-Up Notifications", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Suppresses calls, banners, and alarms during matches", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Switch(
                            checked = isDndEnabled,
                            onCheckedChange = { isDndEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = BeastRed, checkedTrackColor = BeastRed.copy(alpha = 0.5f))
                        )
                    }

                    // Mistouch Lock Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.TouchApp, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Mistouch Prevention & Gesture Lock", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Locks status bar pull-down and back navigation gestures", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                        Switch(
                            checked = isMistouchLocked,
                            onCheckedChange = { isMistouchLocked = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldYellow, checkedTrackColor = GoldYellow.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }

        // Screen Refresh Rate Switcher
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberBorder,
                cornerRadius = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "DISPLAY REFRESH RATE OVERRIDE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Force high refresh rate rendering for games that support 90Hz, 120Hz, or 144Hz panels.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(60, 90, 120, 144).forEach { rate ->
                            val isSelected = targetRefreshRate == rate
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) CyberCyan else CyberSurfaceVariant)
                                    .clickable { targetRefreshRate = rate }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${rate}Hz",
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
