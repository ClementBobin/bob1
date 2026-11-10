package com.mirage.bob1ob1ob1ob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)
