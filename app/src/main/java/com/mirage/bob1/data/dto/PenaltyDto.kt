package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.Penalty
import kotlinx.serialization.Serializable

@Serializable
data class PenaltyDto(
    val id: String,
    val seasonId: Int,
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
        seasonId       = seasonId,
        userId         = userId,
        matchId        = matchId,
        reason         = reason,
        points         = points,
        kickedOut      = kickedOut,
        acknowledgedAt = acknowledgedAt,
        timestampIso   = createdAt,
    )
}