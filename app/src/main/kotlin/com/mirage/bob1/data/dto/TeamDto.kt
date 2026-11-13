package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.Team
import kotlinx.serialization.Serializable

@Serializable
data class TeamDto(
    val id: String,
    val name: String,
    val divisionId: String = "",
    val isActive: Boolean = true,
) {
    fun toDomain() = Team(id, name, divisionId)
}
