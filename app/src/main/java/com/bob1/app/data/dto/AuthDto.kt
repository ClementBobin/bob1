package com.bob1.app.data.dto

import com.bob1.app.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
)

@Serializable
data class LoginResponseDto(val token: String, val user: UserDto)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    // API now sends UserRole as integer (0 = Official, 1 = Admin).
    // coerceInputValues=true in HttpClient lets us fall back gracefully on unknown values.
    val role: Int = 0,
) {
    fun toDomain() = User(
        id        = id,
        email     = email,
        firstName = firstName,
        lastName  = lastName,
        role      = when (role) {
            1    -> UserRole.ADMIN
            else -> UserRole.OFFICIAL
        },
    )

    companion object {
        fun fromDomain(u: User) = UserDto(
            id        = u.id,
            email     = u.email,
            firstName = u.firstName,
            lastName  = u.lastName,
            role      = when (u.role) {
                UserRole.ADMIN    -> 1
                UserRole.OFFICIAL -> 0
            },
        )
    }
}

enum class UserRole { OFFICIAL, ADMIN }