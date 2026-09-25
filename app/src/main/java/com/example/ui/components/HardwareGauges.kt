package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HardwareTelemetry
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

@Composable
fun HardwareGaugeArc(
    value: Float,
    maxValue: Float,
    title: String,
    unit: String,
    modifier: Modifier = Modifier,
    accentColor: Color = CyberCyan
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (value / maxValue).coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "gaugeAnim"
    )

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val strokeWidth = 10.dp.toPx()
            val startAngle = 135f
            val sweepAngle = 270f

            // Background track
            drawArc(
                color = Color(0xFF1F293D),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset.Zero
            )

            // Active gauge
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(CyberPurple, accentColor, CyberCyan)
                ),
                startAngle = startAngle,
                sweepAngle = sweepAngle * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(size.width, size.height),
                topLeft = Offset.Zero
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format(Locale.US, "%.0f", value),
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 32.sp,
                letterSpacing = (-1).sp
            )
            Text(
                text = unit.uppercase(),
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TelemetryOverviewGrid(
    telemetry: HardwareTelemetry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live FPS Block
            TelemetryMetricCard(
                icon = Icons.Default.Speed,
                label = "Real FPS",
                value = String.format(Locale.US, "%.1f", telemetry.liveFps),
                subtext = "Target: ${telemetry.targetFps} FPS",
                accentColor = if (telemetry.liveFps >= 58) NeonGreen else GoldYellow,
                modifier = Modifier.weight(1f).testTag("metric_card_fps")
            )

            // Temperature Block
            val tempColor = if (telemetry.batteryTemperatureCelsius > 40f) BeastRed
            else if (telemetry.batteryTemperatureCelsius > 36f) GoldYellow else NeonGreen
            TelemetryMetricCard(
                icon = Icons.Default.Thermostat,
                label = "Thermal State",
                value = String.format(Locale.US, "%.1f°C", telemetry.batteryTemperatureCelsius),
                subtext = "Battery: ${telemetry.batteryPercent}%${if (telemetry.isCharging) " ⚡" else ""}",
                accentColor = tempColor,
                modifier = Modifier.weight(1f).testTag("metric_card_temp")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // RAM Usage Block
            TelemetryMetricCard(
                icon = Icons.Default.Memory,
                label = "Memory (RAM)",
                value = "${telemetry.ramUsedPercent}%",
                subtext = String.format(Locale.US, "%.1f / %.1f GB", telemetry.ramUsedGb, telemetry.ramTotalGb),
                accentColor = CyberCyan,
                modifier = Modifier.weight(1f).testTag("metric_card_ram")
            )

            // Ping Latency Block
            val pingColor = if (telemetry.pingMs < 45) NeonGreen else if (telemetry.pingMs < 85) GoldYellow else BeastRed
            TelemetryMetricCard(
                icon = Icons.Default.Wifi,
                label = "Network Latency",
                value = "${telemetry.pingMs} ms",
                subtext = telemetry.networkType,
                accentColor = pingColor,
                modifier = Modifier.weight(1f).testTag("metric_card_ping")
            )
        }
    }
}

@Composable
fun TelemetryMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = label,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = TextMuted,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}
