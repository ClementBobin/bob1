package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: List<String> = emptyList(),
    val familyId: String? = null,
) {
    fun toDomain() = User(
        id, email, firstName, lastName,
        roles.mapNotNull { r -> UserRole.entries.firstOrNull { it.name.equals(r, true) } }.toSet(),
        familyId,
    )

    companion object {
        fun fromDomain(u: User) = UserDto(
            id = u.id,
            email = u.email,
            firstName = u.firstName,
            lastName = u.lastName,
            roles = u.roles.map { it.name },
            familyId = u.familyId,
        )
    }
}
