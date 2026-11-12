package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchQueryDto(
    val divisionId: String? = null,
    val memberId: String? = null,
    val from: String? = null,
    val to: String? = null,
    val status: String? = null,
    val search: String? = null,
    // With a season the API lists the whole season; without one it defaults to the next 120 days.
    val season: String? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
