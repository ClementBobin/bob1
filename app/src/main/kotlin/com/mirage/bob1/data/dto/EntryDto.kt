package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class EntryDto(
    val id: String,
    val category: String,
    val points: Int,
    val reason: String = "",
    val matchId: String? = null,
    val createdAtUtc: String,
    val personId: String = "",
    val personName: String = "",
    // Manual adjustments are created by an admin; a revoked one no longer counts.
    val isManual: Boolean = false,
    val isRevoked: Boolean = false,
) {
    fun toDomain() = PointEntry(id, category, points, reason, parseUtc(createdAtUtc), isManual, isRevoked)
}
