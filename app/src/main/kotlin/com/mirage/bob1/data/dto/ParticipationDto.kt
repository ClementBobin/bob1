package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

@Serializable
data class ParticipationDto(val presenceConfirmed: Boolean = false, val slots: List<SlotDto> = emptyList()) {
    fun toDomain() = Participation(presenceConfirmed, slots.map { it.toDomain() })
}
