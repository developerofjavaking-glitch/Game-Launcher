package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameAppItem
import com.example.data.model.HardwareTelemetry
import com.example.data.model.PerformanceModeType
import com.example.ui.components.AddGamePickerModal
import com.example.ui.components.CyberBadge
import com.example.ui.components.GameGridItemCard
import com.example.ui.components.HeroFeaturedGameCard
import com.example.ui.components.NoGamesInstalledCard
import com.example.ui.components.QuickBoosterButton
import com.example.ui.components.TelemetryOverviewGrid
import com.example.ui.theme.BeastRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun GamesScreen(
    games: List<GameAppItem>,
    telemetry: HardwareTelemetry,
    currentMode: PerformanceModeType,
    isBoosting: Boolean,
    onQuickBoost: () -> Unit,
    onLaunchGame: (GameAppItem) -> Unit,
    onToggleFavorite: (GameAppItem) -> Unit,
    onAddApp: (GameAppItem) -> Unit,
    allDeviceApps: List<GameAppItem>,
    modifier: Modifier = Modifier
) {
    var showAddPicker by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val featuredGame = remember(games) {
        games.firstOrNull { it.isFavorite } ?: games.firstOrNull()
    }

    val filteredGames = remember(games, selectedFilter, searchQuery) {
        games.filter { game ->
            val matchesFilter = when (selectedFilter) {
                "Favorites" -> game.isFavorite
                "Installed" -> game.isInstalled
                "Shooters" -> game.genre.contains("FPS", ignoreCase = true) || game.genre.contains("Shooter", ignoreCase = true) || game.genre.contains("Battle", ignoreCase = true)
                "RPGs" -> game.genre.contains("RPG", ignoreCase = true) || game.genre.contains("Action", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() || game.title.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    if (showAddPicker) {
        AddGamePickerModal(
            availableApps = allDeviceApps,
            onSelectApp = { app ->
                onAddApp(app)
                showAddPicker = false
            },
            onDismiss = { showAddPicker = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
            .testTag("games_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "JAIRO",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "PLAYER",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Next-Gen Gaming Hub & Engine",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                QuickBoosterButton(
                    onClick = onQuickBoost,
                    isBoosting = isBoosting
                )
            }
        }

        // Active Mode & Live FPS Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(currentMode.colorHex))
                    )
                    Text(
                        text = currentMode.title.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = Color(currentMode.colorHex),
                        letterSpacing = 0.5.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.0f FPS", telemetry.liveFps),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = CyberCyan
                    )
                    Text(text = "•", color = TextMuted)
                    Text(
                        text = "${telemetry.pingMs}ms",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (telemetry.pingMs < 45) NeonGreen else BeastRed
                    )
                    Text(text = "•", color = TextMuted)
                    Text(
                        text = String.format(Locale.US, "%.1f°C", telemetry.batteryTemperatureCelsius),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Hero Featured Game or No Games Installed Card
        if (featuredGame != null) {
            item {
                HeroFeaturedGameCard(
                    game = featuredGame,
                    onLaunch = { onLaunchGame(featuredGame) },
                    onToggleFavorite = { onToggleFavorite(featuredGame) }
                )
            }
        } else {
            item {
                NoGamesInstalledCard(
                    onAddAppClick = { showAddPicker = true }
                )
            }
        }

        // Hardware Mini Telemetry Grid
        item {
            TelemetryOverviewGrid(telemetry = telemetry)
        }

        // Game Library Section Header & Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "MY GAMES",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 0.5.sp
                        )
                        CyberBadge(
                            text = "${filteredGames.size}",
                            color = CyberCyan
                        )
                    }

                    Button(
                        onClick = { showAddPicker = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberSurfaceVariant,
                            contentColor = CyberCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Game",
                            modifier = Modifier.size(16.dp),
                            tint = CyberCyan
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ADD GAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Filter Chips
                val filterOptions = listOf("All", "Favorites", "Installed", "Shooters", "RPGs")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyberCyan else CyberSurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) CyberCyan else CyberBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.Black else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Games List
        if (filteredGames.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberSurfaceCard)
                        .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
                        .padding(vertical = 32.dp, horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = CyberCyan.copy(alpha = 0.6f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = if (games.isEmpty()) "No games installed on this device" else "No games found in '$selectedFilter'",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = if (games.isEmpty())
                                "Download games from Google Play Store or tap 'Add Game' to select any installed app to launch with JairoPlayer."
                            else
                                "Try selecting 'All' or search for a different game title.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {
                                if (games.isEmpty()) showAddPicker = true else selectedFilter = "All"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (games.isEmpty()) "Add Installed App" else "Show All Games",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredGames, key = { it.packageName }) { game ->
                GameGridItemCard(
                    game = game,
                    onLaunch = { onLaunchGame(game) },
                    onToggleFavorite = { onToggleFavorite(game) }
                )
            }
        }
    }
}
