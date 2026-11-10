package com.mirage.bob1ob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1ob1.domain.model.*

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
