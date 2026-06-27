package com.bob1.app.domain.model

import com.bob1.app.data.dto.UserRole

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
)
