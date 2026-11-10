package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RankingQueryDto(
    val category: String? = null,
    val season: String? = null,
    val view: String? = null,
    val search: String? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
