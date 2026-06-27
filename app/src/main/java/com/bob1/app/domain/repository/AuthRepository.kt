package com.bob1.app.domain.repository

import com.bob1.app.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    fun register(registerRequest: Any)
}