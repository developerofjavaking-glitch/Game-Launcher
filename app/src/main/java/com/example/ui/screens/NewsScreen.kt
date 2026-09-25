package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.db.BookmarkedNewsEntity
import com.example.data.model.NewsArticle
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
import com.example.ui.theme.BeastRed
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.CyberSurfaceCard
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NewsScreen(
    newsList: List<NewsArticle>,
    bookmarkedNews: List<BookmarkedNewsEntity>,
    onToggleBookmark: (NewsArticle, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var viewingArticle by remember { mutableStateOf<NewsArticle?>(null) }

    val categories = listOf("All", "Esports", "Optimization", "Patches", "Releases")
    val bookmarkedIds = remember(bookmarkedNews) { bookmarkedNews.map { it.newsId }.toSet() }

    val filteredNews = remember(newsList, selectedCategory, searchQuery) {
        newsList.filter { article ->
            val matchesCategory = selectedCategory == "All" || article.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    article.title.contains(searchQuery, ignoreCase = true) ||
                    article.excerpt.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    if (viewingArticle != null) {
        val article = viewingArticle!!
        val isBookmarked = bookmarkedIds.contains(article.id)

        Dialog(onDismissRequest = { viewingArticle = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = CyberSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp)
                    .testTag("news_detail_dialog")
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CyberBadge(text = article.category, color = CyberCyan)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onToggleBookmark(article, isBookmarked) }) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) GoldYellow else TextSecondary
                                )
                            }
                            IconButton(onClick = { viewingArticle = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }

                    // Article Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            lineHeight = 26.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "By ${article.author}",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "•", color = TextMuted)
                            Text(
                                text = "${article.timeAgo} • ${article.readMinutes} min read",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Banner image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            val drawableRes = article.bannerDrawableRes ?: R.drawable.img_news_banner
                            Image(
                                painter = painterResource(id = drawableRes),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Text(
                            text = article.fullContent,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )

                        // Tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            article.tags.forEach { tag ->
                                Text(
                                    text = "#$tag",
                                    color = CyberPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(horizontal = 16.dp)
            .testTag("news_screen_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "GAMING NEWS & FEEDS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Esports, Engine Patches & Optimization Updates",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search updates, patches, esports...", fontSize = 12.sp, color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = CyberBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Category Filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberCyan else CyberSurfaceCard)
                            .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Featured Top Story Card
        val topStory = filteredNews.firstOrNull()
        if (topStory != null) {
            item {
                CyberCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewingArticle = topStory },
                    cornerRadius = 16.dp,
                    borderColor = CyberBorder
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Image(
                                painter = painterResource(id = topStory.bannerDrawableRes ?: R.drawable.img_news_banner),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xDD090C11))
                                        )
                                    )
                            )
                            CyberBadge(
                                text = "TOP STORY",
                                color = BeastRed,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = topStory.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                            Text(
                                text = topStory.excerpt,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                    Text(text = "${topStory.timeAgo} • ${topStory.readMinutes}m read", color = TextMuted, fontSize = 11.sp)
                                }
                                Text(
                                    text = "READ ARTICLE →",
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Remaining News Items
        val remainingNews = if (filteredNews.isNotEmpty()) filteredNews.drop(1) else emptyList()
        items(remainingNews, key = { it.id }) { item ->
            val isBookmarked = bookmarkedIds.contains(item.id)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberSurfaceCard)
                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                    .clickable { viewingArticle = item }
                    .padding(14.dp)
                    .testTag("news_item_${item.id}")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CyberBadge(text = item.category, color = CyberCyan)
                        IconButton(
                            onClick = { onToggleBookmark(item, isBookmarked) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) GoldYellow else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = item.excerpt,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 2
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "By ${item.author}", color = TextMuted, fontSize = 10.sp)
                        Text(text = "•", color = TextMuted)
                        Text(text = item.timeAgo, color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
