package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomProSettings
import com.example.data.model.HardwareTelemetry
import com.example.data.model.PerformanceModeType
import com.example.data.telemetry.BoostResult
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.components.HardwareGaugeArc
import com.example.ui.theme.BeastRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderBright
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMagenta
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
fun BoosterScreen(
    telemetry: HardwareTelemetry,
    currentMode: PerformanceModeType,
    customSettings: CustomProSettings,
    isBoosting: Boolean,
    lastBoostResult: BoostResult?,
    onSelectMode: (PerformanceModeType) -> Unit,
    onUpdateCustomSettings: (CustomProSettings) -> Unit,
    onTriggerBoost: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
            .testTag("booster_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "PERFORMANCE ENGINE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "HyperBoost & System Optimization Modes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Performance Gauge Centerpiece
        item {
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(currentMode.colorHex),
                glowColor = Color(currentMode.colorHex),
                cornerRadius = 20.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HardwareGaugeArc(
                        value = telemetry.performanceIndexScore.toFloat(),
                        maxValue = 100f,
                        title = "Gaming Index",
                        unit = "Score",
                        accentColor = Color(currentMode.colorHex)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ACTIVE PROFILE: ${currentMode.title.uppercase()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(currentMode.colorHex)
                    )
                    Text(
                        text = currentMode.subtitle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // One-Tap Boost / RAM Cleaner Section
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
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "SYSTEM RAM CLEANER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = String.format(Locale.US, "%.1f / %.1f GB (%.0f%%)", telemetry.ramUsedGb, telemetry.ramTotalGb, telemetry.ramUsedPercent.toFloat()),
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // RAM Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSurfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (telemetry.ramUsedPercent / 100f).coerceIn(0f, 1f))
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(CyberCyan, CyberPurple)
                                    )
                                )
                        )
                    }

                    // Animated boost feedback result
                    AnimatedVisibility(
                        visible = lastBoostResult != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        if (lastBoostResult != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonGreen.copy(alpha = 0.15f))
                                    .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "+${lastBoostResult.freedMemoryMb} MB RAM freed! ${lastBoostResult.optimizedProcessesCount} background services paused.",
                                        fontSize = 11.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onTriggerBoost,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("boost_system_ram_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBoosting) "OPTIMIZING HARDWARE..." else "ONE-TAP RAM CLEAN & BOOST",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Optimization Mode Selection Cards
        item {
            Text(
                text = "SELECT PERFORMANCE MODE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 0.5.sp
            )
        }

        items(PerformanceModeType.values().size) { idx ->
            val mode = PerformanceModeType.values()[idx]
            val isSelected = currentMode == mode
            val modeColor = Color(mode.colorHex)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) modeColor.copy(alpha = 0.08f) else CyberSurfaceCard)
                    .border(
                        1.5.dp,
                        if (isSelected) modeColor else CyberBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectMode(mode) }
                    .padding(14.dp)
                    .testTag("mode_card_${mode.name}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(modeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (mode) {
                            PerformanceModeType.BEAST_TURBO -> Icons.Default.Bolt
                            PerformanceModeType.BALANCED -> Icons.Default.Speed
                            PerformanceModeType.BATTERY_SAVER -> Icons.Default.Eco
                            PerformanceModeType.CUSTOM_PRO -> Icons.Default.Tune
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = mode.title,
                            tint = modeColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = mode.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            if (isSelected) {
                                CyberBadge(text = "ACTIVE", color = modeColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = mode.subtitle,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target: ${mode.targetFps} FPS",
                                color = modeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "•", color = TextMuted)
                            Text(
                                text = mode.touchSamplingRate,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Custom Pro Mode Fine-Tuning Panel (shows when Custom Pro selected)
        if (currentMode == PerformanceModeType.CUSTOM_PRO) {
            item {
                CyberCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberMagenta,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "CUSTOM PRO TUNER",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = CyberMagenta
                        )

                        // Target FPS Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Framerate Cap (Target FPS)", color = TextSecondary, fontSize = 12.sp)
                                Text("${customSettings.targetFps} FPS", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Slider(
                                value = customSettings.targetFps.toFloat(),
                                onValueChange = {
                                    onUpdateCustomSettings(customSettings.copy(targetFps = it.toInt()))
                                },
                                valueRange = 30f..144f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberCyan,
                                    activeTrackColor = CyberCyan
                                )
                            )
                        }

                        // Resolution Scale Slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Dynamic Resolution Scale", color = TextSecondary, fontSize = 12.sp)
                                Text("${customSettings.resolutionScalePercent}%", color = CyberMagenta, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Slider(
                                value = customSettings.resolutionScalePercent.toFloat(),
                                onValueChange = {
                                    onUpdateCustomSettings(customSettings.copy(resolutionScalePercent = it.toInt()))
                                },
                                valueRange = 50f..100f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberMagenta,
                                    activeTrackColor = CyberMagenta
                                )
                            )
                        }

                        // Anti-Aliasing Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Hardware MSAA 4x Anti-Aliasing", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Smoother edges in 3D gaming pipelines", color = TextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = customSettings.antiAliasingEnabled,
                                onCheckedChange = {
                                    onUpdateCustomSettings(customSettings.copy(antiAliasingEnabled = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = CyberMagenta, checkedTrackColor = CyberPurple)
                            )
                        }

                        // Network Dual-Band Boost Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Dual-Channel Wi-Fi + 5G Boost", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Reduces packet loss and multiplayer jitter", color = TextMuted, fontSize = 10.sp)
                            }
                            Switch(
                                checked = customSettings.networkDualBandBoost,
                                onCheckedChange = {
                                    onUpdateCustomSettings(customSettings.copy(networkDualBandBoost = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberCyan.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        }
    }
}
