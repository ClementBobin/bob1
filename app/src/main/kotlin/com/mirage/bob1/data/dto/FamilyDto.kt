package com.mirage.bob1ob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1ob1.domain.model.*

@Serializable
data class FamilyDto(
    val id: String,
    val name: String,
    val members: List<MemberDto> = emptyList(),
) {
    fun toDomain() = Family(id, name, members.map { it.toDomain() })
}
