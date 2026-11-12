package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class MatchDto(
    val id: String,
    val title: String,
    val divisionId: String,
    val divisionName: String = "",
    val homeTeamId: String,
    val awayTeamId: String,
    val venueId: String,
    val startsAtUtc: String,
    val status: String,
    // Club season of the match, e.g. "2026-2027".
    val season: String = "",
) {
    fun toDomain() = Match(
        id, title, divisionId, divisionName, homeTeamId, awayTeamId, venueId, parseUtc(startsAtUtc),
        MatchStatus.entries.firstOrNull { it.name.equals(status, true) } ?: MatchStatus.Scheduled,
    )
}
