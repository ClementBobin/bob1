package com.mirage.bob1ob1.domain.model

data class SeasonPointRanking(
    val rank: Int,
    val userId: String,
    val fullName: String,
    val totalPoints: Int,
)

data class SeasonPointEntry(
    val id: String,
    val seasonId: Int,
    val userId: String,
    val matchId: String?,
    val points: Int,
    val createdAt: String,
    val updatedAt: String,
)

data class SeasonPointSummary(
    val seasonId: Int,
    val userId: String,
    val totalPoints: Int,
    val entries: List<SeasonPointEntry>,
)