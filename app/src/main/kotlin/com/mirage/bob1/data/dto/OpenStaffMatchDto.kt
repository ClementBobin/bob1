package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

@Serializable
data class OpenStaffMatchDto(
    val matchId: String,
    val title: String,
    val divisionName: String = "",
    val startsAtUtc: String,
    val slots: List<SlotDto> = emptyList(),
) {
    fun toDomain() = OpenStaffMatch(matchId, title, divisionName, parseUtc(startsAtUtc), slots.map { it.toDomain() })
}
