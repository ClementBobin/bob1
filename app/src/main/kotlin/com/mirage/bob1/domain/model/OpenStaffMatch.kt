package com.mirage.bob1.domain.model

import java.time.Instant

/** A match with staff slots (MAR, arbitres, chrono...). */
data class OpenStaffMatch(
    val matchId: String,
    val title: String,
    val startsAt: Instant,
    val slots: List<Slot>,
) {
    val missing get() = slots.sumOf { it.missing }
    val mine get() = slots.any { it.mine }
    val isFull get() = missing == 0
}
