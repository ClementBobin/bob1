package com.bob1.app.domain.model

import com.bob1.app.data.dto.MatchSubscriptionStatus
import com.bob1.app.data.dto.OfficialRole

data class Match(
    val id: String,
    val divisionId: String,
    val divisionName: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val dateIso: String,        // ISO-8601 e.g. "2026-06-27T15:00:00Z"
    val location: String,
    val slots: List<RoleSlot>,  // configurable per match
    val emergencyDate: String?,
    val emergencyPoints: Int,
    val subscriptionStatus: MatchSubscriptionStatus = MatchSubscriptionStatus.NEUTRAL,
    val currentUserRole: OfficialRole? = null,
)
