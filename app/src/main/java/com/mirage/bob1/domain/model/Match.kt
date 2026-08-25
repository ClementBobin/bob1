package com.mirage.bob1.domain.model

import com.mirage.bob1.data.dto.MatchSubscriptionStatus
import com.mirage.bob1.data.dto.OfficialRole

data class Match(
    val id: String,
    val divisionId: String,
    val divisionName: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val dateIso: String,
    // Location — name always present; address + coords populated when API has them
    val location: String,           // display name, e.g. "Gymnase Pasteur"
    val locationAddress: String,    // full address for geocoded nav fallback
    val locationLat: Double?,       // null when not geocoded
    val locationLng: Double?,
    val slots: List<RoleSlot>,
    /** True when at least one role slot is still open. Populated from MinMatchDto (list view). */
    val areSlotsAvailable: Boolean = false,
    val subscriptionStatus: MatchSubscriptionStatus = MatchSubscriptionStatus.NEUTRAL,
    val currentUserRole: OfficialRole? = null,
)