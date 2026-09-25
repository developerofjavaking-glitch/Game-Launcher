package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import com.example.R
import com.example.data.model.GameAppItem
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

@Composable
fun HeroFeaturedGameCard(
    game: GameAppItem,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_game_card"),
        glowColor = CyberCyan,
        cornerRadius = 20.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            // Background Artwork
            Image(
                painter = painterResource(id = R.drawable.img_game_hero),
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Cyber Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x99080B11),
                                Color(0xF2080B11)
                            )
                        )
                    )
            )

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberBadge(
                    text = "FEATURED TITLE",
                    color = CyberCyan
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x80000000))
                ) {
                    Icon(
                        imageVector = if (game.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (game.isFavorite) BeastRed else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom Info & Launch Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = game.title,
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp
                    )
                    CyberBadge(
                        text = "${game.estimatedFps} FPS",
                        color = NeonGreen
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${game.developer} • ${game.genre}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Playtime: ${game.totalTimeFormatted} • Last: ${game.lastPlayedAgo}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onLaunch,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(42.dp)
                            .testTag("hero_launch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Launch",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "LAUNCH",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameGridItemCard(
    game: GameAppItem,
    onLaunch: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurfaceCard)
            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onLaunch)
            .padding(12.dp)
            .testTag("game_item_${game.packageName}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Game Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (game.icon != null) {
                    val bitmap = remember(game.icon) {
                        try {
                            game.icon.toBitmap(96, 96).asImageBitmap()
                        } catch (e: Exception) {
                            null
                        }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = game.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = game.title,
                            tint = CyberCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = game.title,
                        tint = CyberCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Game Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = game.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    if (game.isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = BeastRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${game.genre} • ${game.totalTimeFormatted}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CyberBadge(
                        text = "${game.estimatedFps} FPS",
                        color = if (game.estimatedFps >= 90) CyberCyan else NeonGreen
                    )
                    Text(
                        text = if (game.isInstalled) "INSTALLED" else "PRESET PROFILE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Quick Play Button
            Button(
                onClick = onLaunch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan.copy(alpha = 0.15f),
                    contentColor = CyberCyan
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "PLAY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AddGamePickerModal(
    availableApps: List<GameAppItem>,
    onSelectApp: (GameAppItem) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredApps = remember(searchQuery, availableApps) {
        if (searchQuery.isBlank()) availableApps
        else availableApps.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CyberSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .testTag("add_game_dialog")
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ADD GAME TO HUB",
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Text(
                    text = "Select any installed game or app to enable JairoPlayer booster profiles and FPS tracking.",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search installed applications...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = CyberBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberSurfaceVariant)
                                .clickable { onSelectApp(app) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberBorder),
                                contentAlignment = Alignment.Center
                            ) {
                                if (app.icon != null) {
                                    val bmp = remember(app.icon) {
                                        try { app.icon.toBitmap(64, 64).asImageBitmap() } catch (e: Exception) { null }
                                    }
                                    if (bmp != null) {
                                        Image(bitmap = bmp, contentDescription = app.title, modifier = Modifier.fillMaxSize())
                                    } else {
                                        Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = CyberCyan)
                                    }
                                } else {
                                    Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = CyberCyan)
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.title,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Text(
                                    text = app.packageName,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
