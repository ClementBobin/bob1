package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithBiometric(): Result<User>
    suspend fun generateAndSaveBiometricToken(): Result<Unit>
    suspend fun removeBiometricToken(): Result<Unit>
    suspend fun register(firstName: String, lastName: String, email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
}