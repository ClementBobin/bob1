package com.mirage.bob1.domain.model

import java.time.Instant

data class Slot(
    val id: String,
    val kind: String,          // "Volunteer" (buvette...) | "Staff" (MAR, arbitre...)
    val role: String,
    val capacity: Int,
    val filled: Int,
    val startsAt: Instant?,
    val endsAt: Instant?,
    val mine: Boolean,
) {
    val missing get() = (capacity - filled).coerceAtLeast(0)
    val isFull get() = missing == 0
}
