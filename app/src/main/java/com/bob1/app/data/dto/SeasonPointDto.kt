package com.bob1.app.data.dto

import kotlinx.serialization.Serializable

/** Entry in the per-user season point history. */
@Serializable
data class SeasonPointDto(
    val id: String,
    val seasonId: Int,
    val userId: String,
    val matchId: String? = null,
    val points: Int,
    val createdAt: String,
    val updatedAt: String,
)

/** One row in the leaderboard returned by GET /api/ranking. */
@Serializable
data class SeasonPointRankingDto(
    val rank: Int,
    val userId: String,
    val fullName: String,
    val totalPoints: Int,
)

/** Summary for a single user returned by GET /api/ranking/me or /api/ranking/users/{userId}. */
@Serializable
data class SeasonPointSummaryDto(
    val seasonId: Int,
    val userId: String,
    val totalPoints: Int,
    val entries: List<SeasonPointDto>,
)