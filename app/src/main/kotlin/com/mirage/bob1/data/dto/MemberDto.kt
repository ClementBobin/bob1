package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class MemberDto(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String = "",
    val isMe: Boolean = false,
    val divisionId: String? = null,
    val divisionName: String? = null,
) {
    fun toDomain() = FamilyMember(userId, firstName, lastName, email, isMe, divisionId, divisionName)
}
