package com.bob1.app.data.dto

import com.bob1.app.domain.model.Team
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: String,
    val name: String,
    val divisionId: String,
    val logoUrl: String? = null,
) {
    fun toDomain() = Team(id = id, name = name, divisionId = divisionId, logoUrl = logoUrl)

    companion object {
        fun fromDomain(t: Team) = TeamDto(
            id = t.id,
            name = t.name,
            divisionId = t.divisionId,
            logoUrl = t.logoUrl
        )
    }
}