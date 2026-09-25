package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BeastRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderBright
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberBorder,
    glowColor: Color? = null,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "borderGlow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alphaAnim"
    )

    val finalBorder = if (glowColor != null) {
        BorderStroke(1.5.dp, glowColor.copy(alpha = alpha))
    } else {
        BorderStroke(1.dp, borderColor)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(finalBorder, RoundedCornerShape(cornerRadius)),
        shape = RoundedCornerShape(cornerRadius),
        color = CyberSurfaceCard,
        shadowElevation = 4.dp
    ) {
        content()
    }
}

@Composable
fun CyberBadge(
    text: String,
    color: Color = CyberCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun QuickBoosterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBoosting: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scalePulse"
    )

    Box(
        modifier = modifier
            .testTag("one_tap_boost_button")
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(CyberPurple, CyberCyan)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Boost",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = if (isBoosting) "BOOSTING..." else "QUICK BOOST",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun CrosshairRenderer(
    styleIndex: Int,
    color: Color = CyberCyan,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val strokeWidth = 2.dp.toPx()

        when (styleIndex) {
            0 -> {
                // Classic Dot
                drawCircle(color = color, radius = 4.dp.toPx(), center = Offset(cx, cy))
            }
            1 -> {
                // Tactical Cross
                drawLine(
                    color = color,
                    start = Offset(cx - 16.dp.toPx(), cy),
                    end = Offset(cx - 4.dp.toPx(), cy),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(cx + 4.dp.toPx(), cy),
                    end = Offset(cx + 16.dp.toPx(), cy),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(cx, cy - 16.dp.toPx()),
                    end = Offset(cx, cy - 4.dp.toPx()),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(cx, cy + 4.dp.toPx()),
                    end = Offset(cx, cy + 16.dp.toPx()),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(cx, cy))
            }
            2 -> {
                // Circle & Dot
                drawCircle(
                    color = color,
                    radius = 12.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = strokeWidth)
                )
                drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(cx, cy))
            }
            else -> {
                // Futuristic Diamond
                drawCircle(color = color, radius = 3.dp.toPx(), center = Offset(cx, cy))
                drawLine(color = color, start = Offset(cx - 12.dp.toPx(), cy), end = Offset(cx, cy - 12.dp.toPx()), strokeWidth = strokeWidth)
                drawLine(color = color, start = Offset(cx, cy - 12.dp.toPx()), end = Offset(cx + 12.dp.toPx(), cy), strokeWidth = strokeWidth)
                drawLine(color = color, start = Offset(cx + 12.dp.toPx(), cy), end = Offset(cx, cy + 12.dp.toPx()), strokeWidth = strokeWidth)
                drawLine(color = color, start = Offset(cx, cy + 12.dp.toPx()), end = Offset(cx - 12.dp.toPx(), cy), strokeWidth = strokeWidth)
            }
        }
    }
}
