package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val gameTitle: String,
    val startTime: Long,
    val endTime: Long,
    val durationMillis: Long,
    val performanceMode: String,
    val avgFps: Float,
    val maxFps: Float,
    val batteryDrainPercent: Float,
    val peakTempCelsius: Float,
    val notes: String = ""
)

@Entity(tableName = "game_profiles")
data class GameProfileEntity(
    @PrimaryKey
    val packageName: String,
    val title: String,
    val isFavorite: Boolean = false,
    val customTargetFps: Int = 60,
    val touchSamplingBoost: Boolean = true,
    val antiAliasing: Boolean = true,
    val dndBlocked: Boolean = true,
    val performanceMode: String = "BEAST_TURBO",
    val totalPlayTimeMillis: Long = 0,
    val lastPlayedTimestamp: Long = 0,
    val launchCount: Int = 0,
    val category: String = "Action"
)

@Entity(tableName = "bookmarked_news")
data class BookmarkedNewsEntity(
    @PrimaryKey
    val newsId: String,
    val title: String,
    val category: String,
    val timestamp: Long
)
