package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class PerformanceModeType(
    val title: String,
    val subtitle: String,
    val targetFps: Int,
    val touchSamplingRate: String,
    val thermalPolicy: String,
    val colorHex: Long,
    val iconName: String
) {
    BEAST_TURBO(
        title = "Beast Turbo",
        subtitle = "Uncapped performance, maximum touch response & frame stability",
        targetFps = 120,
        touchSamplingRate = "480Hz Ultra",
        thermalPolicy = "Aggressive Cooling / No Throttle",
        colorHex = 0xFFFF2E63, // Neon Red
        iconName = "Bolt"
    ),
    BALANCED(
        title = "Balanced Mode",
        subtitle = "Stable 60 FPS with optimal power and thermal balance",
        targetFps = 60,
        touchSamplingRate = "240Hz Fast",
        thermalPolicy = "Adaptive Dynamic",
        colorHex = 0xFF00F0FF, // Cyber Cyan
        iconName = "Speed"
    ),
    BATTERY_SAVER(
        title = "Battery Saver",
        subtitle = "Extends play duration, limits background drain & 45 FPS cap",
        targetFps = 45,
        touchSamplingRate = "120Hz Normal",
        thermalPolicy = "Cool & Efficient",
        colorHex = 0xFF10B981, // Neon Green
        iconName = "Eco"
    ),
    CUSTOM_PRO(
        title = "Custom Pro Mode",
        subtitle = "Manual tuning of FPS limits, resolution scale & touch sensitivity",
        targetFps = 90,
        touchSamplingRate = "User Defined",
        thermalPolicy = "Custom Limits",
        colorHex = 0xFFD946EF, // Hyper Magenta
        iconName = "Tune"
    )
}

data class CustomProSettings(
    val targetFps: Int = 90,
    val resolutionScalePercent: Int = 100,
    val touchSensitivityMultiplier: Float = 1.5f,
    val antiAliasingEnabled: Boolean = true,
    val networkDualBandBoost: Boolean = true,
    val thermalThresholdCelsius: Float = 44.0f
)
