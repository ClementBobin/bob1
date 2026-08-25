package com.mirage.bob1.data.repository

import com.mirage.bob1.data.dto.LoginRequestDto
import com.mirage.bob1.data.dto.RegisterRequestDto
import com.mirage.bob1.data.local.SessionManager
import com.mirage.bob1.data.remote.AuthAPI
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val authAPI: AuthAPI,
    private val session: SessionManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = authAPI.login(LoginRequestDto(email, password))
        session.saveToken(response.token, response.expiresTime)
        val user = authAPI.getCurrentUser(response.token).toDomain()
        session.saveSession(user)
        user
    }

    override suspend fun loginWithBiometric(): Result<User> = runCatching {
        val bioToken = session.getBiometricToken()
            ?: error("Aucun token biométrique enregistré.")
        val response = authAPI.biometricLogin(bioToken)
        session.saveToken(response.token, response.expiresTime)
        val user = authAPI.getCurrentUser(response.token).toDomain()
        session.saveSession(user)
        user
    }

    override suspend fun generateAndSaveBiometricToken(): Result<Unit> = runCatching {
        val response = authAPI.generateBiometricToken()
        session.saveBiometricToken(response.token)
    }

    override suspend fun removeBiometricToken(): Result<Unit> = runCatching {
        authAPI.removeBiometricToken()
        session.clearBiometricToken()
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ): Result<User> = runCatching {
        authAPI.register(
            RegisterRequestDto(
                email     = email,
                password  = password,
                firstName = firstName,
                lastName  = lastName,
            )
        )
        // Auto-login after registering
        val response = authAPI.login(LoginRequestDto(email, password))
        session.saveToken(response.token, response.expiresTime)
        val user = authAPI.getCurrentUser(response.token).toDomain()
        session.saveSession(user)
        user
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        authAPI.logout()
        session.clearSession()
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        val token = session.currentToken() ?: error("Aucun token de session disponible.")
        authAPI.getCurrentUser(token).toDomain()
    }
}