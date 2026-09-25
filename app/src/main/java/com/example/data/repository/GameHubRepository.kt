package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.R
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkedNewsEntity
import com.example.data.db.GameProfileEntity
import com.example.data.db.GameSessionEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.CustomProSettings
import com.example.data.model.GameAppItem
import com.example.data.model.NewsArticle
import com.example.data.model.PerformanceModeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameHubRepository(
    private val context: Context,
    private val database: AppDatabase,
    private val coroutineScope: CoroutineScope
) {
    private val gameDao = database.gameDao()
    private val packageManager: PackageManager = context.packageManager

    // Active in-game session state
    private val _activeSession = MutableStateFlow(ActiveSessionState())
    val activeSession: StateFlow<ActiveSessionState> = _activeSession.asStateFlow()

    // Global performance mode
    private val _globalPerformanceMode = MutableStateFlow(PerformanceModeType.BEAST_TURBO)
    val globalPerformanceMode: StateFlow<PerformanceModeType> = _globalPerformanceMode.asStateFlow()

    // Custom pro settings
    private val _customProSettings = MutableStateFlow(CustomProSettings())
    val customProSettings: StateFlow<CustomProSettings> = _customProSettings.asStateFlow()

    // Installed game list cache
    private val _installedGames = MutableStateFlow<List<GameAppItem>>(emptyList())
    val installedGames: StateFlow<List<GameAppItem>> = _installedGames.asStateFlow()

    // All sessions from DB
    val allSessions: Flow<List<GameSessionEntity>> = gameDao.getAllSessions()

    // Bookmarked news IDs
    val bookmarkedNews: Flow<List<BookmarkedNewsEntity>> = gameDao.getBookmarkedNews()

    // News feed list
    private val _newsFeed = MutableStateFlow(getPrepopulatedNews())
    val newsFeed: StateFlow<List<NewsArticle>> = _newsFeed.asStateFlow()

    init {
        coroutineScope.launch {
            seedInitialGameProfilesIfNeeded()
            refreshGamesList()
        }
    }

    suspend fun refreshGamesList() = withContext(Dispatchers.IO) {
        val dbProfiles = gameDao.getAllProfiles().first()
        val profileMap = dbProfiles.associateBy { it.packageName }

        val realInstalledPackages = mutableListOf<GameAppItem>()

        // 1. Scan device applications for games & user apps
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    intent,
                    PackageManager.ResolveInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(intent, 0)
            }

            for (info in resolveInfos) {
                val pkgName = info.activityInfo.packageName
                if (pkgName == context.packageName) continue // Skip self

                val appInfo = info.activityInfo.applicationInfo
                val isGameCategory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    appInfo.category == ApplicationInfo.CATEGORY_GAME
                } else {
                    false
                }
                val isGameFlag = (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
                val knownInDb = profileMap.containsKey(pkgName)

                // If categorized as game, or user added to DB, or matches common gaming tokens
                val label = info.loadLabel(packageManager).toString()
                val isProbableGame = isGameCategory || isGameFlag || knownInDb ||
                        isGamingKeyword(label, pkgName)

                if (isProbableGame) {
                    val profile = profileMap[pkgName] ?: GameProfileEntity(
                        packageName = pkgName,
                        title = label,
                        category = "Mobile Game"
                    )

                    realInstalledPackages.add(
                        GameAppItem(
                            packageName = pkgName,
                            title = label,
                            icon = info.loadIcon(packageManager),
                            isInstalled = true,
                            profile = profile,
                            developer = "Installed App",
                            genre = if (isGameCategory) "Native Game" else "Installed Game",
                            installSize = "Optimized",
                            estimatedFps = 60,
                            isFavorite = profile.isFavorite,
                            lastPlayedAgo = formatTimestampAgo(profile.lastPlayedTimestamp),
                            totalTimeFormatted = formatDuration(profile.totalPlayTimeMillis)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Add pre-populated popular games that are in DB (or mock presets)
        val defaultPresets = getPopularPresets()
        for (preset in defaultPresets) {
            val exists = realInstalledPackages.any { it.packageName == preset.packageName }
            if (!exists) {
                val profile = profileMap[preset.packageName] ?: GameProfileEntity(
                    packageName = preset.packageName,
                    title = preset.title,
                    category = preset.genre
                )
                realInstalledPackages.add(
                    preset.copy(
                        profile = profile,
                        isFavorite = profile.isFavorite,
                        lastPlayedAgo = formatTimestampAgo(profile.lastPlayedTimestamp),
                        totalTimeFormatted = formatDuration(profile.totalPlayTimeMillis)
                    )
                )
            }
        }

        _installedGames.value = realInstalledPackages.sortedWith(
            compareByDescending<GameAppItem> { it.isFavorite }
                .thenByDescending { it.profile.totalPlayTimeMillis }
                .thenBy { it.title }
        )
    }

    private fun isGamingKeyword(label: String, pkg: String): Boolean {
        val lowered = "$label $pkg".lowercase()
        return lowered.contains("game") || lowered.contains("play") ||
                lowered.contains("craft") || lowered.contains("racing") ||
                lowered.contains("rpg") || lowered.contains("fps") ||
                lowered.contains("combat") || lowered.contains("arena") ||
                lowered.contains("genshin") || lowered.contains("pubg") ||
                lowered.contains("freefire") || lowered.contains("cod") ||
                lowered.contains("mobile") || lowered.contains("legends")
    }

    suspend fun queryAllDeviceApps(): List<GameAppItem> = withContext(Dispatchers.IO) {
        val apps = mutableListOf<GameAppItem>()
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = packageManager.queryIntentActivities(intent, 0)
            for (info in resolveInfos) {
                val pkgName = info.activityInfo.packageName
                if (pkgName == context.packageName) continue
                val label = info.loadLabel(packageManager).toString()
                apps.add(
                    GameAppItem(
                        packageName = pkgName,
                        title = label,
                        icon = info.loadIcon(packageManager),
                        isInstalled = true,
                        profile = GameProfileEntity(packageName = pkgName, title = label)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        apps.sortedBy { it.title }
    }

    suspend fun addAppToGameHub(app: GameAppItem) = withContext(Dispatchers.IO) {
        gameDao.insertOrUpdateProfile(
            GameProfileEntity(
                packageName = app.packageName,
                title = app.title,
                isFavorite = true,
                category = "Custom Game"
            )
        )
        refreshGamesList()
    }

    suspend fun toggleFavorite(packageName: String, current: Boolean) = withContext(Dispatchers.IO) {
        gameDao.setFavorite(packageName, !current)
        refreshGamesList()
    }

    suspend fun removeGameProfile(packageName: String) = withContext(Dispatchers.IO) {
        gameDao.deleteProfile(packageName)
        refreshGamesList()
    }

    fun setGlobalPerformanceMode(mode: PerformanceModeType) {
        _globalPerformanceMode.value = mode
    }

    fun updateCustomProSettings(settings: CustomProSettings) {
        _customProSettings.value = settings
    }

    // --- Session Tracking ---
    fun launchGameSession(
        game: GameAppItem,
        currentBatteryPct: Int,
        currentTempCelsius: Float,
        liveFps: Float
    ) {
        val mode = _globalPerformanceMode.value
        _activeSession.value = ActiveSessionState(
            isActive = true,
            game = game,
            startTime = System.currentTimeMillis(),
            currentDurationMillis = 0L,
            currentFps = liveFps,
            fpsSamples = listOf(liveFps),
            startBatteryLevel = currentBatteryPct,
            startTemp = currentTempCelsius,
            peakTemp = currentTempCelsius,
            performanceMode = mode,
            isDndActive = true,
            is4DHapticsActive = true
        )

        // Try launching actual native intent if installed
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(game.packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        } catch (e: Exception) {
            // Ignored, simulator UI will remain active
        }

        // Start session monitoring loop
        coroutineScope.launch {
            while (_activeSession.value.isActive) {
                delay(1000)
                _activeSession.update { current ->
                    if (!current.isActive) return@update current
                    val newDuration = System.currentTimeMillis() - current.startTime
                    // Sample live fps simulation
                    val target = when (current.performanceMode) {
                        PerformanceModeType.BEAST_TURBO -> 120.0f
                        PerformanceModeType.BALANCED -> 60.0f
                        PerformanceModeType.BATTERY_SAVER -> 45.0f
                        PerformanceModeType.CUSTOM_PRO -> _customProSettings.value.targetFps.toFloat()
                    }
                    val jitter = ((System.currentTimeMillis() % 7) - 3) * 0.4f
                    val sampleFps = (target + jitter).coerceIn(20f, 144f)
                    val updatedSamples = current.fpsSamples + sampleFps
                    val updatedPeak = maxOf(current.peakTemp, current.startTemp + (newDuration / 120000f))

                    current.copy(
                        currentDurationMillis = newDuration,
                        currentFps = sampleFps,
                        fpsSamples = if (updatedSamples.size > 300) updatedSamples.takeLast(300) else updatedSamples,
                        peakTemp = updatedPeak
                    )
                }
            }
        }
    }

    suspend fun endActiveGameSession(currentBatteryPct: Int): GameSessionEntity? = withContext(Dispatchers.IO) {
        val session = _activeSession.value
        if (!session.isActive || session.game == null) return@withContext null

        val endTime = System.currentTimeMillis()
        val duration = endTime - session.startTime
        val avgFps = session.averageFps
        val maxFps = session.fpsSamples.maxOrNull() ?: avgFps
        val batteryDrain = (session.startBatteryLevel - currentBatteryPct).coerceAtLeast(0).toFloat()

        val entity = GameSessionEntity(
            packageName = session.game.packageName,
            gameTitle = session.game.title,
            startTime = session.startTime,
            endTime = endTime,
            durationMillis = duration,
            performanceMode = session.performanceMode.title,
            avgFps = avgFps,
            maxFps = maxFps,
            batteryDrainPercent = batteryDrain,
            peakTempCelsius = session.peakTemp,
            notes = "Performance Mode: ${session.performanceMode.title} | Target FPS: ${session.performanceMode.targetFps}"
        )

        // Save to Room DB
        gameDao.insertSession(entity)
        gameDao.recordGamePlay(session.game.packageName, duration, endTime)

        // Reset active session
        _activeSession.value = ActiveSessionState(isActive = false)

        refreshGamesList()
        entity
    }

    fun updateInGameToggles(
        dnd: Boolean? = null,
        haptics: Boolean? = null,
        crosshair: Boolean? = null,
        crosshairStyle: Int? = null,
        mistouchLock: Boolean? = null
    ) {
        _activeSession.update { current ->
            current.copy(
                isDndActive = dnd ?: current.isDndActive,
                is4DHapticsActive = haptics ?: current.is4DHapticsActive,
                isCrosshairEnabled = crosshair ?: current.isCrosshairEnabled,
                crosshairStyleIndex = crosshairStyle ?: current.crosshairStyleIndex,
                isMistouchLockActive = mistouchLock ?: current.isMistouchLockActive
            )
        }
    }

    suspend fun clearSessionHistory() = withContext(Dispatchers.IO) {
        gameDao.clearAllSessions()
    }

    suspend fun toggleNewsBookmark(news: NewsArticle, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        if (isBookmarked) {
            gameDao.removeBookmark(news.id)
        } else {
            gameDao.bookmarkNews(
                BookmarkedNewsEntity(
                    newsId = news.id,
                    title = news.title,
                    category = news.category,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private suspend fun seedInitialGameProfilesIfNeeded() = withContext(Dispatchers.IO) {
        val existing = gameDao.getAllProfiles().first()
        if (existing.isEmpty()) {
            val presets = getPopularPresets().map { it.profile }
            gameDao.insertProfilesIfNotExists(presets)
        }
    }

    private fun getPopularPresets(): List<GameAppItem> {
        return listOf(
            GameAppItem(
                packageName = "com.miHoYo.GenshinImpact",
                title = "Genshin Impact",
                isInstalled = false,
                profile = GameProfileEntity(
                    packageName = "com.miHoYo.GenshinImpact",
                    title = "Genshin Impact",
                    isFavorite = true,
                    category = "Open World RPG",
                    performanceMode = "BEAST_TURBO",
                    customTargetFps = 90
                ),
                developer = "HoYoverse",
                genre = "Action RPG",
                installSize = "24.6 GB",
                estimatedFps = 60,
                isFavorite = true
            ),
            GameAppItem(
                packageName = "com.tencent.ig",
                title = "PUBG Mobile",
                isInstalled = false,
                profile = GameProfileEntity(
                    packageName = "com.tencent.ig",
                    title = "PUBG Mobile",
                    isFavorite = true,
                    category = "Battle Royale",
                    performanceMode = "BEAST_TURBO",
                    customTargetFps = 120
                ),
                developer = "Krafton / Tencent",
                genre = "Battle Royale FPS",
                installSize = "4.2 GB",
                estimatedFps = 120,
                isFavorite = true
            ),
            GameAppItem(
                packageName = "com.activision.callofduty.shooter",
                title = "Call of Duty: Mobile",
                isInstalled = false,
                profile = GameProfileEntity(
                    packageName = "com.activision.callofduty.shooter",
                    title = "Call of Duty: Mobile",
                    isFavorite = false,
                    category = "Tactical FPS",
                    performanceMode = "BEAST_TURBO",
                    customTargetFps = 120
                ),
                developer = "Activision Publishing",
                genre = "Action / Shooter",
                installSize = "5.8 GB",
                estimatedFps = 120
            ),
            GameAppItem(
                packageName = "com.dts.freefireth",
                title = "Free Fire MAX",
                isInstalled = false,
                profile = GameProfileEntity(
                    packageName = "com.dts.freefireth",
                    title = "Free Fire MAX",
                    isFavorite = false,
                    category = "Survival Shooter"
                ),
                developer = "Garena International",
                genre = "Battle Royale",
                installSize = "2.1 GB",
                estimatedFps = 90
            ),
            GameAppItem(
                packageName = "com.gameloft.android.ANMP.GloftA9HM",
                title = "Asphalt 9: Legends",
                isInstalled = false,
                profile = GameProfileEntity(
                    packageName = "com.gameloft.android.ANMP.GloftA9HM",
                    title = "Asphalt 9: Legends",
                    isFavorite = false,
                    category = "Arcade Racing"
                ),
                developer = "Gameloft",
                genre = "Arcade Racing",
                installSize = "3.4 GB",
                estimatedFps = 60
            ),
            GameAppItem(
                packageName = "com.mobile.legends",
                title = "Mobile Legends: Bang Bang",
                isInstalled = false,
                profile = GameProfileEntity(
                    packageName = "com.mobile.legends",
                    title = "Mobile Legends: Bang Bang",
                    isFavorite = false,
                    category = "MOBA"
                ),
                developer = "Moonton",
                genre = "5v5 MOBA",
                installSize = "3.9 GB",
                estimatedFps = 120
            )
        )
    }

    private fun getPrepopulatedNews(): List<NewsArticle> {
        return listOf(
            NewsArticle(
                id = "news_01",
                title = "Android 15 Vulkan 1.3 Update Delivers Up to 35% Higher GPU Efficiency in Mobile Titles",
                excerpt = "The latest Android graphics stack overhaul introduces Dynamic Rendering and Pipeline Compilation Caching for Snapdragon 8 Gen 3 & Dimensity 9300.",
                fullContent = "Mobile gamers on modern Android flagships are seeing major improvements in frame pacing and thermal sustainability thanks to the broader rollout of the Vulkan 1.3 graphics API. Game developers utilizing modern shader compilers report a 35% decrease in thermal throttling over extended 60-minute gaming sessions. In JairoPlayer, pairing Beast Turbo mode with hardware Vulkan pipelines ensures maximum sustained 120 FPS in heavy combat sequences.",
                category = "Optimization",
                author = "Jairo Tech Review",
                timeAgo = "1 hour ago",
                readMinutes = 3,
                tags = listOf("Vulkan", "GPU", "FPS Boost", "Snapdragon"),
                bannerDrawableRes = R.drawable.img_game_hero
            ),
            NewsArticle(
                id = "news_02",
                title = "World Esports Mobile Championship 2026: Prize Pool Reaches Historic $8,000,000",
                excerpt = "The premier global mobile esports tournament announces regional qualifiers across Asia, Americas, and Europe.",
                fullContent = "The International Esports Federation has revealed the roadmap for the 2026 Mobile World Cup. Featuring marquee battle royale and MOBA tournaments, the event will spotlight the world's top handheld athletes. Device telemetry partners confirmed that competitive units will run on low-latency 144Hz displays with sub-15ms touch polling response times.",
                category = "Esports",
                author = "Arena Esports",
                timeAgo = "3 hours ago",
                readMinutes = 4,
                tags = listOf("Esports", "Tournament", "Prize Pool", "Championship"),
                bannerDrawableRes = R.drawable.img_news_banner
            ),
            NewsArticle(
                id = "news_03",
                title = "PUBG Mobile 3.6 Patch Notes: 120 FPS Ultra Mode Now Live on Supported Flagships",
                excerpt = "The newest patch brings high-tier weapon rebalances, new futuristic tactical gear, and optimized 120 frames per second rendering.",
                fullContent = "Krafton has deployed update 3.6 featuring new anti-aliasing algorithms and higher framerate modes. Players using JairoPlayer's Beast Turbo profile can unlock smooth 120 FPS rendering without micro-stutters during intense hot-drops in Pochinki and Military Base.",
                category = "Patches",
                author = "Mobile Tactical Gaming",
                timeAgo = "5 hours ago",
                readMinutes = 2,
                tags = listOf("PUBG Mobile", "Patch Notes", "120 FPS", "Battle Royale"),
                targetGame = "PUBG Mobile"
            ),
            NewsArticle(
                id = "news_04",
                title = "Genshin Impact Version 5.8: New Snezhnaya Region Teased With Ray Tracing Enhancements",
                excerpt = "HoYoverse previews spectacular snowy landscapes, new Cryo heroes, and hardware accelerated reflections.",
                fullContent = "The travelers' journey continues northward toward the frosty domain of the Tsaritsa. Developer diaries reveal that mobile players will enjoy advanced ambient occlusion and volumetric fog effects with zero battery penalty when running adaptive performance modes.",
                category = "Releases",
                author = "Teyvat Chronicle",
                timeAgo = "Yesterday",
                readMinutes = 5,
                tags = listOf("Genshin Impact", "HoYoverse", "RPG", "Update"),
                targetGame = "Genshin Impact"
            ),
            NewsArticle(
                id = "news_05",
                title = "How 480Hz Touch Sampling Rates Give Mobile Gamers an Unfair Advantage in Ranked Matches",
                excerpt = "A deep dive into touch digitizer frequency, input latency, and how game boosters eliminate swipe delay.",
                fullContent = "Input latency is the silent enemy of mobile shooters. When your screen digitizer samples at 480Hz rather than standard 120Hz, input registration delay drops from 8.3ms down to 2.1ms. Combined with JairoPlayer's Mistouch Lock and Touch Boost, players can flick-aim and trigger shots milliseconds faster than opponents.",
                category = "Optimization",
                author = "Jairo Hardware Lab",
                timeAgo = "2 days ago",
                readMinutes = 4,
                tags = listOf("Touch Latency", "Pro Gaming", "Hardware", "Aiming")
            )
        )
    }

    private fun formatTimestampAgo(timestamp: Long): String {
        if (timestamp <= 0) return "Never"
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24
        return when {
            minutes < 2 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            else -> "${days}d ago"
        }
    }

    private fun formatDuration(millis: Long): String {
        if (millis <= 0) return "0m"
        val totalMinutes = millis / (1000 * 60)
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
    }
}
