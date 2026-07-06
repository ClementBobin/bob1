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
    val timestampIso: String,
) {
    fun toDomain() = Penalty(
        id = id,
        userId = userId,
        matchId = matchId,
        reason = reason,
        points = points,
        kickedOut = kickedOut,
        acknowledgedAt = acknowledgedAt,
        timestampIso = timestampIso
    )

    companion object {
        fun fromDomain(p: Penalty) = PenaltyDto(
            id = p.id,
            userId = p.userId,
            matchId = p.matchId,
            reason = p.reason,
            points = p.points,
            kickedOut = p.kickedOut,
            acknowledgedAt = p.acknowledgedAt,
            timestampIso = p.timestampIso
        )
    }
}
