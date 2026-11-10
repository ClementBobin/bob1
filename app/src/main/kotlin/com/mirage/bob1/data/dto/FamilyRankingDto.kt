package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

@Serializable
data class FamilyRankingDto(
    val season: String = "",
    val total: Int = 0,
    val top: List<FamilyRankingRowDto> = emptyList(),
    val mine: FamilyRankingRowDto? = null,
) {
    fun toDomain() = Ranking(season, total, top.map { it.toDomain() }, mine?.toDomain())
}
