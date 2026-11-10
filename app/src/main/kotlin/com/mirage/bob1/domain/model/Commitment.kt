package com.mirage.bob1.domain.model

import java.time.Instant

data class Commitment(
    val type: CommitmentType,
    val matchId: String,
    val matchTitle: String,
    val startsAt: Instant,
    val label: String,
)
