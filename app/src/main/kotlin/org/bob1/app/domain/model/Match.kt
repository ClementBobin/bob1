package org.bob1.app.domain.model

import java.time.Instant

data class Match(
    val id: String,
    val title: String,
    val divisionId: String,
    val divisionName: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val venueId: String,
    val startsAt: Instant,
    val status: MatchStatus,
)
