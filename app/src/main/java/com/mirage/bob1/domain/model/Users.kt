package com.mirage.bob1.domain.model

import com.mirage.bob1.data.dto.UserRole

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
)
