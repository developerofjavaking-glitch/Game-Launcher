package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.BookmarkedNewsEntity
import com.example.data.db.GameSessionEntity
import com.example.data.model.ActiveSessionState
import com.example.data.model.AppGameSettings
import com.example.data.model.CustomProSettings
import com.example.data.model.GameAppItem
import com.example.data.model.HardwareTelemetry
import com.example.data.model.NewsArticle
import com.example.data.model.PerformanceModeType
import com.example.data.model.RecordedClipItem
import com.example.data.model.RecordingStatusState
import com.example.data.recorder.GameRecorderManager
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
    STATS("Stats"),
    SETTINGS("Settings")
}

class MainGameHubViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = GameHubRepository(application, database, viewModelScope)
    private val telemetryManager = HardwareTelemetryManager(application, viewModelScope)
    private val recorderManager = GameRecorderManager(application, viewModelScope)

    // Current navigation tab
    private val _currentTab = MutableStateFlow(GameHubTab.GAMES)
    val currentTab: StateFlow<GameHubTab> = _currentTab.asStateFlow()

    // Application Settings (Video resolution 1080p/720p/480p, floating window, etc.)
    private val _settings = MutableStateFlow(AppGameSettings())
    val settings: StateFlow<AppGameSettings> = _settings.asStateFlow()

    // Floating window active status
    private val _isFloatingOverlayActive = MutableStateFlow(false)
    val isFloatingOverlayActive: StateFlow<Boolean> = _isFloatingOverlayActive.asStateFlow()

    // Screen recording state & gallery clips
    val recordingState: StateFlow<RecordingStatusState> = recorderManager.recordingState
    val recordedClips: StateFlow<List<RecordedClipItem>> = recorderManager.recordedClips

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

    fun updateSettings(newSettings: AppGameSettings) {
        _settings.value = newSettings
    }

    fun toggleFloatingOverlay(show: Boolean? = null) {
        _isFloatingOverlayActive.value = show ?: !_isFloatingOverlayActive.value
        telemetryManager.triggerVibration("light")
    }

    fun startRecording(gameTitle: String = "Game") {
        recorderManager.startRecording(gameTitle, _settings.value)
        telemetryManager.triggerVibration("light")
    }

    fun stopRecording(onComplete: (Uri?, String) -> Unit = { _, _ -> }) {
        recorderManager.stopRecording { uri, fileName ->
            telemetryManager.triggerVibration("boost")
            onComplete(uri, fileName)
        }
    }

    fun captureScreenshot(gameTitle: String = "Game", onComplete: (Uri?, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val result = recorderManager.captureScreenshot(gameTitle)
            telemetryManager.triggerVibration("light")
            onComplete(result.first, result.second)
        }
    }

    fun deleteRecordedClip(clip: RecordedClipItem) {
        recorderManager.deleteClip(clip)
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
        if (_settings.value.autoBoostOnLaunch) {
            triggerQuickBoost()
        }
        if (_settings.value.showFloatingWindowOnLaunch) {
            _isFloatingOverlayActive.value = true
        }
        repository.launchGameSession(
            game = game,
            currentBatteryPct = currentTel.batteryPercent,
            currentTempCelsius = currentTel.batteryTemperatureCelsius,
            liveFps = currentTel.liveFps
        )
    }

    fun endActiveSession() {
        if (recordingState.value.isRecording) {
            stopRecording()
        }
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

    fun refreshGames() {
        viewModelScope.launch {
            repository.refreshGamesList()
            loadAllDeviceApps()
        }
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
