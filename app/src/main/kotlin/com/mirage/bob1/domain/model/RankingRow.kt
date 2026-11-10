package org.bob1.app.domain.model

data class RankingRow(
    val rank: Int,
    val id: String,
    val name: String,
    val subtitle: String?,
    val points: Int,
    val isMine: Boolean,
)
