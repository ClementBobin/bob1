package com.bob1.app.data.dto

import com.bob1.app.domain.model.Match
import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val id: String,
    val divisionId: String,
    val divisionName: String,
    val homeTeam: TeamDto,
    val awayTeam: TeamDto,
    val dateIso: String,
    val location: String,
    val slots: List<RoleSlotDto>,
    val emergencyDate: String? = null,
    val emergencyPoints: Int = 0,
    val subscriptionStatus: String = "NEUTRAL",
    val currentUserRole: String? = null,
) {
    fun toDomain() = Match(
        id = id,
        divisionId = divisionId,
        divisionName = divisionName,
        homeTeam = homeTeam.toDomain(),
        awayTeam = awayTeam.toDomain(),
        dateIso = dateIso,
        location = location,
        slots = slots.map { it.toDomain() },
        emergencyDate = emergencyDate,
        emergencyPoints = emergencyPoints,
        subscriptionStatus = MatchSubscriptionStatus.valueOf(subscriptionStatus),
        currentUserRole = currentUserRole?.let { OfficialRole.valueOf(it) },
    )

    companion object {
        fun fromDomain(m: Match) = MatchDto(
            id = m.id,
            divisionId = m.divisionId,
            divisionName = m.divisionName,
            homeTeam = TeamDto.fromDomain(m.homeTeam),
            awayTeam = TeamDto.fromDomain(m.awayTeam),
            dateIso = m.dateIso,
            location = m.location,
            slots = m.slots.map { RoleSlotDto.fromDomain(it) },
            emergencyDate = m.emergencyDate,
            emergencyPoints = m.emergencyPoints,
            subscriptionStatus = m.subscriptionStatus.name,
            currentUserRole = m.currentUserRole?.name
        )
    }
}

@Serializable
data class CreateMatchRequestDto(
    val homeTeamId: String,
    val awayTeamId: String,
    val divisionId: String,
    val dateIso: String,
    val location: String,
    val arbitreSlots: Int,
    val chronoSlots: Int,
    val marSlots: Int,
    val emergencyDate: String?,
    val emergencyPoints: Int,
)