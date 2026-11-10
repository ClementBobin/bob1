package com.mirage.bob1.domain.model

data class RankingRow(
    val rank: Int,
    val id: String,
    val name: String,
    val subtitle: String?,
    val points: Int,
    val isMine: Boolean,
)
