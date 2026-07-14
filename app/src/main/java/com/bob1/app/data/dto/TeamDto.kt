package com.bob1.app.data.dto

import com.bob1.app.domain.model.Team
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
