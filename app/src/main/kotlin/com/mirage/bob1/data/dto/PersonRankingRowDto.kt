package com.mirage.bob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1.domain.model.*

@Serializable
data class PersonRankingRowDto(
    val rank: Int,
    val personId: String,
    val personName: String,
    val familyName: String? = null,
    val points: Int,
    val isMine: Boolean = false,
) {
    fun toDomain() = RankingRow(rank, personId, personName, familyName, points, isMine)
}
