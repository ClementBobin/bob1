package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class PersonRankingDto(
    val season: String = "",
    val total: Int = 0,
    val top: List<PersonRankingRowDto> = emptyList(),
    val mine: PersonRankingRowDto? = null,
) {
    fun toDomain() = Ranking(season, total, top.map { it.toDomain() }, mine?.toDomain())
}
