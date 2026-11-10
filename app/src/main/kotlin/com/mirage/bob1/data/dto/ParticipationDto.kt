package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class ParticipationDto(val presenceConfirmed: Boolean = false, val slots: List<SlotDto> = emptyList()) {
    fun toDomain() = Participation(presenceConfirmed, slots.map { it.toDomain() })
}
