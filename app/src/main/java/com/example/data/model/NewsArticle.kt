package com.example.data.model

data class NewsArticle(
    val id: String,
    val title: String,
    val excerpt: String,
    val fullContent: String,
    val category: String, // Esports, Patches, Releases, Optimization, Tournaments
    val author: String,
    val timeAgo: String,
    val readMinutes: Int,
    val bannerDrawableRes: Int? = null,
    val tags: List<String>,
    val targetGame: String? = null
)
