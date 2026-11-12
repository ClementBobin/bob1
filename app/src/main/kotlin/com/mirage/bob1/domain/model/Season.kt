package com.mirage.bob1.domain.model

import java.time.LocalDate

/** A club season: 1 September to 31 August, identified by its label (`2026-2027`). Past seasons are read-only. */
data class Season(
    val label: String,
    val startsOn: LocalDate,
    val endsOn: LocalDate,
    val isCurrent: Boolean,
)
