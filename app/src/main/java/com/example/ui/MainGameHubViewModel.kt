package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkedNewsEntity
import com.example.data.db.GameSessionEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.CustomProSettings
import com.example.data.model.GameAppItem
import com.example.data.model.HardwareTelemetry
import com.example.data.model.NewsArticle
import com.example.data.model.PerformanceModeType
import com.example.data.repository.GameHubRepository
import com.example.data.telemetry.BoostResult
import com.example.data.telemetry.HardwareTelemetryManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class GameHubTab(val label: String) {
    GAMES("Games"),
    BOOSTER("Booster"),
    TOOLS("Tools"),
    NEWS("News"),
    STATS("Stats")
}

class MainGameHubViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = GameHubRepository(application, database, viewModelScope)
    private val telemetryManager = HardwareTelemetryManager(application, viewModelScope)

    // Current navigation tab
    private val _currentTab = MutableStateFlow(GameHubTab.GAMES)
    val currentTab: StateFlow<GameHubTab> = _currentTab.asStateFlow()

    // Boosting status
    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private val _lastBoostResult = MutableStateFlow<BoostResult?>(null)
    val lastBoostResult: StateFlow<BoostResult?> = _lastBoostResult.asStateFlow()

    // All device installed apps (for picker)
    private val _allDeviceApps = MutableStateFlow<List<GameAppItem>>(emptyList())
    val allDeviceApps: StateFlow<List<GameAppItem>> = _allDeviceApps.asStateFlow()

    // Repository flows
    val games: StateFlow<List<GameAppItem>> = repository.installedGames
    val telemetry: StateFlow<HardwareTelemetry> = telemetryManager.telemetry
    val currentMode: StateFlow<PerformanceModeType> = repository.globalPerformanceMode
    val customProSettings: StateFlow<CustomProSettings> = repository.customProSettings
    val activeSession: StateFlow<ActiveSessionState> = repository.activeSession

    val sessionsHistory: StateFlow<List<GameSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val newsFeed: StateFlow<List<NewsArticle>> = repository.newsFeed
    val bookmarkedNews: StateFlow<List<BookmarkedNewsEntity>> = repository.bookmarkedNews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadAllDeviceApps()
    }

    fun selectTab(tab: GameHubTab) {
        _currentTab.value = tab
    }

    fun triggerQuickBoost() {
        if (_isBoosting.value) return
        viewModelScope.launch {
            _isBoosting.value = true
            val result = telemetryManager.boostSystem()
            _lastBoostResult.value = result
            _isBoosting.value = false
        }
    }

    fun launchGame(game: GameAppItem) {
        val currentTel = telemetry.value
        repository.launchGameSession(
            game = game,
            currentBatteryPct = currentTel.batteryPercent,
            currentTempCelsius = currentTel.batteryTemperatureCelsius,
            liveFps = currentTel.liveFps
        )
    }

    fun endActiveSession() {
        viewModelScope.launch {
            repository.endActiveGameSession(telemetry.value.batteryPercent)
        }
    }

    fun toggleFavorite(game: GameAppItem) {
        viewModelScope.launch {
            repository.toggleFavorite(game.packageName, game.isFavorite)
        }
    }

    fun setPerformanceMode(mode: PerformanceModeType) {
        telemetryManager.triggerVibration("light")
        repository.setGlobalPerformanceMode(mode)
    }

    fun updateCustomProSettings(settings: CustomProSettings) {
        repository.updateCustomProSettings(settings)
    }

    fun addAppToHub(app: GameAppItem) {
        viewModelScope.launch {
            repository.addAppToGameHub(app)
        }
    }

    fun testVibration() {
        telemetryManager.triggerVibration("boost")
    }

    fun toggleNewsBookmark(news: NewsArticle, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.toggleNewsBookmark(news, isBookmarked)
        }
    }

    fun clearSessionHistory() {
        viewModelScope.launch {
            repository.clearSessionHistory()
        }
    }

    fun updateInGameToggles(
        dnd: Boolean? = null,
        haptics: Boolean? = null,
        crosshair: Boolean? = null,
        crosshairStyle: Int? = null,
        mistouchLock: Boolean? = null
    ) {
        repository.updateInGameToggles(dnd, haptics, crosshair, crosshairStyle, mistouchLock)
    }

    private fun loadAllDeviceApps() {
        viewModelScope.launch {
            _allDeviceApps.value = repository.queryAllDeviceApps()
        }
    }

    override fun onCleared() {
        super.onCleared()
        telemetryManager.onDestroy()
    }
}
