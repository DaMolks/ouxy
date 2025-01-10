package com.damolks.ouxy.data.model

data class MarketplaceModule(
    val id: String,
    val name: String,
    val description: String,
    val author: String,
    val version: String,
    val minAppVersion: String,
    val stars: Int,
    val repoUrl: String,
    val screenshotUrls: List<String> = emptyList(),
    val downloadCount: Int = 0
)