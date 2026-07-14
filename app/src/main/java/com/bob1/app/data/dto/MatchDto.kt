package com.bob1.app.data.dto

import com.bob1.app.domain.model.Match
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: String,
    val division: DivisionDto,
    val homeTeam: TeamDto,
    val awayTeam: TeamDto,
    val dateUtc: String,
    val emergencyDateUtc: String? = null,
    val emergencyPoints: Int = 0,
    val location: LocationDto,
    val slots: List<RoleSlotDto>,
    val currentUserStatus: String? = null,
) {
    fun toDomain() = Match(
        id                 = id,
        divisionId         = division.id,
        divisionName       = division.name,
        homeTeam           = homeTeam.toDomain(),
        awayTeam           = awayTeam.toDomain(),
        dateIso            = dateUtc,
        location           = location.name,
        locationAddress    = location.address,
        locationLat        = location.latitude,
        locationLng        = location.longitude,
        slots              = slots.map { it.toDomain() },
        emergencyDate      = emergencyDateUtc,
        emergencyPoints    = emergencyPoints,
        subscriptionStatus = MatchSubscriptionStatus.fromApiString(currentUserStatus ?: "Neutral"),
        currentUserRole    = null,
    )
}

@Serializable
data class LocationDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isGeocoded: Boolean = false,
)
