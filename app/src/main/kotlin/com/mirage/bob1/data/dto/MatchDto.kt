package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

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
) {
    fun toDomain() = Match(
        id, title, divisionId, divisionName, homeTeamId, awayTeamId, venueId, parseUtc(startsAtUtc),
        MatchStatus.entries.firstOrNull { it.name.equals(status, true) } ?: MatchStatus.Scheduled,
    )
}
