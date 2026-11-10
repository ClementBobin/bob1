package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val email: String, val password: String)
