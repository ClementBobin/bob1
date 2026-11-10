package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class SlotDto(
    val id: String,
    val kind: String,
    val role: String,
    val capacity: Int,
    val filled: Int,
    val startsAtUtc: String? = null,
    val endsAtUtc: String? = null,
    val mine: Boolean = false,
) {
    fun toDomain() = Slot(id, kind, role, capacity, filled, startsAtUtc?.let(::parseUtc), endsAtUtc?.let(::parseUtc), mine)
}