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
    val currentUserStatus: Int? = null, // API now sends MatchSubscriptionStatus as integer
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
        subscriptionStatus = MatchSubscriptionStatus.fromApiInt(currentUserStatus ?: 0),
        currentUserRole    = null,
    )
}

/** Lightweight match DTO returned by GET /api/matches (list view). */
@Serializable
data class MinMatchDto(
    val id: String,
    val dateUtc: String,
    val division: DivisionDto,
    val location: LocationDto,
    val areSlotsAvailable: Boolean,
    val currentUserStatus: Int? = null, // MatchSubscriptionStatus as integer
)

@Serializable
data class LocationDto(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isGeocoded: Boolean = false,
    val geocodedAt: String? = null, // New field in API v1
)