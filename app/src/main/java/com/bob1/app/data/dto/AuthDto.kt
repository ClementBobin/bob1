package com.bob1.app.data.dto

import com.bob1.app.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class LoginResponseDto(val token: String, val user: UserDto)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String, // "OFFICIAL" | "ADMIN"
) {
    fun toDomain() = User(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        role = if (role == "ADMIN") UserRole.ADMIN else UserRole.OFFICIAL,
    )

    companion object {
        fun fromDomain(u: User) = UserDto(
            id        = u.id,
            email     = u.email,
            firstName = u.firstName,
            lastName  = u.lastName,
            role      = u.role.name,
        )
    }
}

enum class UserRole { OFFICIAL, ADMIN }