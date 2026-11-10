package com.mirage.bob1ob1ob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val expiresInSeconds: Int = 0,
    val user: UserDto,
)
