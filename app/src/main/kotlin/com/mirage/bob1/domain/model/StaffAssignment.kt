package com.mirage.bob1.domain.model

import java.time.Instant

data class StaffAssignment(
    val slotId: String,
    val matchId: String,
    val matchTitle: String,
    val startsAt: Instant,
    val role: String,
)
