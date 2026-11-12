package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.Season
import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class SeasonDto(
    val label: String,
    // The API sends DateOnly as "2026-09-01".
    val startsOn: String,
    val endsOn: String,
    val isCurrent: Boolean = false,
) {
    fun toDomain() = Season(label, LocalDate.parse(startsOn), LocalDate.parse(endsOn), isCurrent)
}
