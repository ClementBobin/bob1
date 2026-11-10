package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

@Serializable
data class FamilyDto(
    val id: String,
    val name: String,
    val members: List<MemberDto> = emptyList(),
) {
    fun toDomain() = Family(id, name, members.map { it.toDomain() })
}
