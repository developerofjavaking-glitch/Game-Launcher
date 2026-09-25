package com.example.data.model

import android.graphics.drawable.Drawable
import com.example.data.db.GameProfileEntity

data class GameAppItem(
    val packageName: String,
    val title: String,
    val icon: Drawable? = null,
    val isInstalled: Boolean = true,
    val profile: GameProfileEntity,
    val developer: String = "Mobile Gaming Studio",
    val genre: String = "Action / RPG",
    val installSize: String = "1.8 GB",
    val estimatedFps: Int = 60,
    val isFavorite: Boolean = false,
    val lastPlayedAgo: String = "Never",
    val totalTimeFormatted: String = "0m"
)

data class HardwareTelemetry(
    val liveFps: Float = 60.0f,
    val targetFps: Int = 60,
    val displayRefreshRate: Int = 60,
    val batteryPercent: Int = 85,
    val batteryTemperatureCelsius: Float = 33.5f,
    val isCharging: Boolean = false,
    val batteryVoltageMv: Int = 4150,
    val ramUsedBytes: Long = 4_200_000_000L,
    val ramTotalBytes: Long = 8_000_000_000L,
    val ramAvailableBytes: Long = 3_800_000_000L,
    val cpuUsagePercent: Int = 38,
    val pingMs: Int = 24,
    val networkType: String = "Wi-Fi 6 (5GHz)",
    val performanceIndexScore: Int = 92
) {
    val ramUsedPercent: Int
        get() = if (ramTotalBytes > 0) ((ramUsedBytes * 100) / ramTotalBytes).toInt() else 50
    val ramUsedGb: Float
        get() = ramUsedBytes / (1024f * 1024f * 1024f)
    val ramTotalGb: Float
        get() = ramTotalBytes / (1024f * 1024f * 1024f)
    val ramAvailableGb: Float
        get() = ramAvailableBytes / (1024f * 1024f * 1024f)
}

data class ActiveSessionState(
    val isActive: Boolean = false,
    val game: GameAppItem? = null,
    val startTime: Long = 0L,
    val currentDurationMillis: Long = 0L,
    val currentFps: Float = 60.0f,
    val fpsSamples: List<Float> = emptyList(),
    val startBatteryLevel: Int = 100,
    val startTemp: Float = 32.0f,
    val peakTemp: Float = 32.0f,
    val performanceMode: PerformanceModeType = PerformanceModeType.BEAST_TURBO,
    // In-game toggles
    val isDndActive: Boolean = true,
    val is4DHapticsActive: Boolean = true,
    val isCrosshairEnabled: Boolean = false,
    val crosshairStyleIndex: Int = 0,
    val isMistouchLockActive: Boolean = false
) {
    val averageFps: Float
        get() = if (fpsSamples.isNotEmpty()) fpsSamples.average().toFloat() else currentFps
}
