package com.mirage.bob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1.domain.model.*

@Serializable
data class FamilyRankingRowDto(
    val rank: Int,
    val familyId: String,
    val familyName: String,
    val points: Int,
    val isMine: Boolean = false,
) {
    fun toDomain() = RankingRow(rank, familyId, familyName, null, points, isMine)
}
