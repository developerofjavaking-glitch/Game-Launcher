package com.example.data.model

import android.net.Uri

enum class VideoResolution(
    val label: String,
    val width: Int,
    val height: Int,
    val description: String,
    val badge: String
) {
    RES_1080P("1080p FHD", 1920, 1080, "Full High Definition (Ultra Clear, 60fps)", "1080P"),
    RES_720P("720p HD", 1280, 720, "High Definition (Smooth & Low Battery)", "720P"),
    RES_480P("480p SD", 854, 480, "Standard Definition (Minimal Storage)", "480P")
}

enum class VideoFps(val label: String, val fps: Int) {
    FPS_30("30 FPS", 30),
    FPS_60("60 FPS", 60),
    FPS_90("90 FPS (Pro)", 90),
    FPS_120("120 FPS (Ultra)", 120)
}

enum class VideoBitrate(val label: String, val bps: Int, val description: String) {
    LOW("4 Mbps", 4_000_000, "Economical storage"),
    MEDIUM("8 Mbps", 8_000_000, "Balanced quality"),
    HIGH("16 Mbps", 16_000_000, "Esports studio quality")
}

enum class AudioSourceOption(val label: String) {
    GAME_AND_MIC("Game Audio + Microphone"),
    INTERNAL_AUDIO("Internal Game Audio"),
    MIC_ONLY("Microphone Only"),
    MUTED("Muted (No Audio)")
}

enum class FloatingBubbleStyle(val label: String, val description: String) {
    COMPACT_PILL("Compact Gaming Pill", "Minimalist horizontal HUD pill on screen edge"),
    XRECORDER_CIRCLE("XRecorder Float Circle", "Classic floating bubble with quick action ring"),
    FULL_HUD("Expanded Esports Bar", "Full telemetry dashboard with instant toggles")
}

data class AppGameSettings(
    val resolution: VideoResolution = VideoResolution.RES_1080P,
    val frameRate: VideoFps = VideoFps.FPS_60,
    val bitrate: VideoBitrate = VideoBitrate.HIGH,
    val audioSource: AudioSourceOption = AudioSourceOption.GAME_AND_MIC,
    val showFloatingWindowOnLaunch: Boolean = true,
    val floatingBubbleStyle: FloatingBubbleStyle = FloatingBubbleStyle.COMPACT_PILL,
    val showFpsInFloatingWindow: Boolean = true,
    val showTempInFloatingWindow: Boolean = true,
    val showRamInFloatingWindow: Boolean = true,
    val autoBoostOnLaunch: Boolean = true,
    val autoHideFloatingWhileRecording: Boolean = false,
    val hapticFeedback: Boolean = true,
    val targetRefreshRateHz: Int = 120,
    val saveFolderLabel: String = "Gallery / Movies / JairoPlayer"
)

data class RecordedClipItem(
    val id: String,
    val fileName: String,
    val uriString: String,
    val durationFormatted: String,
    val sizeFormatted: String,
    val dateAddedFormatted: String,
    val resolution: String,
    val gameName: String
)

data class RecordingStatusState(
    val isRecording: Boolean = false,
    val elapsedSeconds: Int = 0,
    val formattedTime: String = "00:00",
    val activeResolution: VideoResolution = VideoResolution.RES_1080P,
    val lastSavedVideoUri: Uri? = null,
    val lastSavedVideoName: String? = null,
    val statusMessage: String? = null
)
