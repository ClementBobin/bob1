package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

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
