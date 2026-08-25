package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.Team
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: String,
    val name: String,
    val logoUrl: String? = null,
    val division: DivisionDto,  // API sends nested division object
) {
    fun toDomain() = Team(
        id         = id,
        name       = name,
        divisionId = division.id,
        logoUrl    = logoUrl,
    )
}
