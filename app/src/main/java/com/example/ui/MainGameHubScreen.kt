package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FloatingGameOverlayWidget
import com.example.ui.screens.ActiveGameSessionDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BoosterScreen
import com.example.ui.screens.GameToolsScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.TextMuted

@Composable
fun MainGameHubScreen(
    viewModel: MainGameHubViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val games by viewModel.games.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val customSettings by viewModel.customProSettings.collectAsStateWithLifecycle()
    val isBoosting by viewModel.isBoosting.collectAsStateWithLifecycle()
    val lastBoostResult by viewModel.lastBoostResult.collectAsStateWithLifecycle()
    val sessionsHistory by viewModel.sessionsHistory.collectAsStateWithLifecycle()
    val newsFeed by viewModel.newsFeed.collectAsStateWithLifecycle()
    val bookmarkedNews by viewModel.bookmarkedNews.collectAsStateWithLifecycle()
    val allDeviceApps by viewModel.allDeviceApps.collectAsStateWithLifecycle()
    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val isFloatingOverlayActive by viewModel.isFloatingOverlayActive.collectAsStateWithLifecycle()
    val recordingState by viewModel.recordingState.collectAsStateWithLifecycle()
    val recordedClips by viewModel.recordedClips.collectAsStateWithLifecycle()

    BackHandler(enabled = currentTab != GameHubTab.GAMES) {
        viewModel.selectTab(GameHubTab.GAMES)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            GamingBottomNavBar(
                currentTab = currentTab,
                onTabSelect = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tabTransition"
            ) { targetTab ->
                when (targetTab) {
                    GameHubTab.GAMES -> GamesScreen(
                        games = games,
                        telemetry = telemetry,
                        currentMode = currentMode,
                        isBoosting = isBoosting,
                        onQuickBoost = { viewModel.triggerQuickBoost() },
                        onLaunchGame = { viewModel.launchGame(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onAddApp = { viewModel.addAppToHub(it) },
                        allDeviceApps = allDeviceApps
                    )
                    GameHubTab.BOOSTER -> BoosterScreen(
                        telemetry = telemetry,
                        currentMode = currentMode,
                        customSettings = customSettings,
                        isBoosting = isBoosting,
                        lastBoostResult = lastBoostResult,
                        onSelectMode = { viewModel.setPerformanceMode(it) },
                        onUpdateCustomSettings = { viewModel.updateCustomProSettings(it) },
                        onTriggerBoost = { viewModel.triggerQuickBoost() }
                    )
                    GameHubTab.TOOLS -> GameToolsScreen(
                        onTestHaptic = { viewModel.testVibration() }
                    )
                    GameHubTab.NEWS -> NewsScreen(
                        newsList = newsFeed,
                        bookmarkedNews = bookmarkedNews,
                        onToggleBookmark = { news, isBookmarked ->
                            viewModel.toggleNewsBookmark(news, isBookmarked)
                        }
                    )
                    GameHubTab.STATS -> AnalyticsScreen(
                        sessions = sessionsHistory,
                        onClearHistory = { viewModel.clearSessionHistory() }
                    )
                    GameHubTab.SETTINGS -> SettingsScreen(
                        settings = settings,
                        recordedClips = recordedClips,
                        isFloatingOverlayActive = isFloatingOverlayActive,
                        onUpdateSettings = { viewModel.updateSettings(it) },
                        onToggleFloatingOverlay = { viewModel.toggleFloatingOverlay() },
                        onDeleteClip = { viewModel.deleteRecordedClip(it) },
                        onTestVibration = { viewModel.testVibration() }
                    )
                }
            }

            // In-App Draggable Floating Window (XRecorder style) when toggled
            if (isFloatingOverlayActive) {
                FloatingGameOverlayWidget(
                    telemetry = telemetry,
                    recordingState = recordingState,
                    settings = settings,
                    gameTitle = games.firstOrNull()?.title ?: "JairoPlayer",
                    onStartRecord = {
                        viewModel.startRecording(games.firstOrNull()?.title ?: "Gameplay")
                        Toast.makeText(context, "Screen recording started! (${settings.resolution.label})", Toast.LENGTH_SHORT).show()
                    },
                    onStopRecord = {
                        viewModel.stopRecording { _, fileName ->
                            Toast.makeText(context, "Saved directly to Gallery: $fileName", Toast.LENGTH_LONG).show()
                        }
                    },
                    onCaptureScreenshot = {
                        viewModel.captureScreenshot(games.firstOrNull()?.title ?: "Gameplay") { _, name ->
                            Toast.makeText(context, "Screenshot saved to Gallery: $name", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onQuickBoost = {
                        viewModel.triggerQuickBoost()
                        Toast.makeText(context, "One-Tap Turbo Boost Applied!", Toast.LENGTH_SHORT).show()
                    },
                    onOpenSettings = {
                        viewModel.selectTab(GameHubTab.SETTINGS)
                    },
                    onToggleCrosshair = {
                        Toast.makeText(context, "Crosshair overlay toggled", Toast.LENGTH_SHORT).show()
                    },
                    onToggleDnd = {
                        Toast.makeText(context, "Gaming DND toggled", Toast.LENGTH_SHORT).show()
                    },
                    onCloseOverlay = {
                        viewModel.toggleFloatingOverlay(false)
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
    }
}

@Composable
fun GamingBottomNavBar(
    currentTab: GameHubTab,
    onTabSelect: (GameHubTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("gaming_bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurfaceCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GameHubTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                val icon = when (tab) {
                    GameHubTab.GAMES -> Icons.Default.SportsEsports
                    GameHubTab.BOOSTER -> Icons.Default.Bolt
                    GameHubTab.TOOLS -> Icons.Default.Tune
                    GameHubTab.NEWS -> Icons.Default.Newspaper
                    GameHubTab.STATS -> Icons.Default.Assessment
                    GameHubTab.SETTINGS -> Icons.Default.Settings
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTabSelect(tab) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) CyberCyan else TextMuted,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CyberCyan else TextMuted
                        )
                    }
                }
            }
        }
    }
}
