package com.mirage.bob1.domain.model

import java.time.Instant

data class Match(
    val id: String,
    val title: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val venueId: String,
    val startsAt: Instant,
    val status: MatchStatus,
)
