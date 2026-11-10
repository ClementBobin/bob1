package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class StaffAssignmentDto(
    val slotId: String,
    val matchId: String,
    val matchTitle: String,
    val startsAtUtc: String,
    val role: String,
) {
    fun toDomain() = StaffAssignment(slotId, matchId, matchTitle, parseUtc(startsAtUtc), role)
}
