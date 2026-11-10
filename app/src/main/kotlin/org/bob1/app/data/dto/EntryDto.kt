package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

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
) {
    fun toDomain() = PointEntry(id, category, points, reason, parseUtc(createdAtUtc))
}
