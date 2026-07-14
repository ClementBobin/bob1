package com.bob1.app.data.dto

import com.bob1.app.domain.model.Penalty
import kotlinx.serialization.Serializable

@Serializable
data class PenaltyDto(
    val id: String,
    val userId: String,
    val matchId: String? = null,
    val reason: String,
    val points: Int,
    val kickedOut: Boolean = false,
    val acknowledgedAt: String? = null,
    val createdAt: String,          // API field name (was timestampIso)
) {
    fun toDomain() = Penalty(
        id             = id,
        userId         = userId,
        matchId        = matchId,
        reason         = reason,
        points         = points,
        kickedOut      = kickedOut,
        acknowledgedAt = acknowledgedAt,
        timestampIso   = createdAt,
    )
}
