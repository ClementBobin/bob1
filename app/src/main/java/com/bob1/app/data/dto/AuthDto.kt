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
    // API sends UserRole as integer (0 = Official, 1 = Admin, 2 = Client).
    val role: List<Int> = listOf(0),
) {
    fun toDomain() = User(
        id        = id,
        email     = email,
        firstName = firstName,
        lastName  = lastName,
        // load all role enum to a list of UserRole
        role      = role.map {
            when (it) {
                0 -> UserRole.OFFICIAL
                1 -> UserRole.ADMIN
                2 -> UserRole.CLIENT
                else -> throw IllegalArgumentException("Unknown role: $it")
            }
        }
    )

    companion object {
        fun fromDomain(u: User) = UserDto(
            id        = u.id,
            email     = u.email,
            firstName = u.firstName,
            lastName  = u.lastName,
            // convert list of UserRole to list of integer
            role      = u.role.map {
                when (it) {
                    UserRole.OFFICIAL -> 0
                    UserRole.ADMIN    -> 1
                    UserRole.CLIENT   -> 2
                }
            }
        )
    }
}

enum class UserRole { OFFICIAL, ADMIN, CLIENT }