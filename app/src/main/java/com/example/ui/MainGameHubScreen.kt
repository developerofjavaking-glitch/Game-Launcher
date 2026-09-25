package com.example.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.ActiveGameSessionDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BoosterScreen
import com.example.ui.screens.GameToolsScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.NewsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MainGameHubScreen(
    viewModel: MainGameHubViewModel,
    modifier: Modifier = Modifier
) {
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

    BackHandler(enabled = currentTab != GameHubTab.GAMES) {
        viewModel.selectTab(GameHubTab.GAMES)
    }

    // Active in-game overlay simulation dialog
    if (activeSession.isActive) {
        ActiveGameSessionDialog(
            sessionState = activeSession,
            telemetry = telemetry,
            onEndSession = { viewModel.endActiveSession() },
            onToggleDnd = { viewModel.updateInGameToggles(dnd = it) },
            onToggleHaptics = { viewModel.updateInGameToggles(haptics = it) },
            onToggleCrosshair = { viewModel.updateInGameToggles(crosshair = it) },
            onToggleMistouch = { viewModel.updateInGameToggles(mistouchLock = it) }
        )
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
                }
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("gaming_bottom_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurfaceCard)
                .border(1.dp, CyberBorder, RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTabSelect(tab) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) CyberCyan else TextMuted
                        )
                    }
                }
            }
        }
    }
}
