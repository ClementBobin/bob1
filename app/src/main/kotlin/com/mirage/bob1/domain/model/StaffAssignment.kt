package org.bob1.app.domain.model

import java.time.Instant

data class StaffAssignment(
    val slotId: String,
    val matchId: String,
    val matchTitle: String,
    val startsAt: Instant,
    val role: String,
)
