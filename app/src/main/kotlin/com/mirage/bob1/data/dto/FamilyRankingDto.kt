package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class FamilyRankingDto(
    val season: String = "",
    val total: Int = 0,
    val top: List<FamilyRankingRowDto> = emptyList(),
    val mine: FamilyRankingRowDto? = null,
) {
    fun toDomain() = Ranking(season, total, top.map { it.toDomain() }, mine?.toDomain())
}
