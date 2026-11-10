package com.mirage.bob1.domain.model

data class Ranking(
    val season: String,
    val total: Int,
    val rows: List<RankingRow>,
    val mine: RankingRow?,
)
