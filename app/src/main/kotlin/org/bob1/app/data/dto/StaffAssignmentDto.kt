package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

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
