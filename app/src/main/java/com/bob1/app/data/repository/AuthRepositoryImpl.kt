package com.bob1.app.data.repository

import com.bob1.app.data.dto.LoginRequestDto
import com.bob1.app.data.dto.RegisterRequestDto
import com.bob1.app.data.local.SessionManager
import com.bob1.app.data.remote.AuthAPI
import com.bob1.app.domain.model.User
import com.bob1.app.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val authAPI: AuthAPI,
    private val session: SessionManager,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = authAPI.login(LoginRequestDto(email, password))
        val user = response.user.toDomain()
        session.saveSession(user, response.token)
        user
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ): Result<User> = runCatching {
        val userDto = authAPI.register(
            RegisterRequestDto(
                email     = email,
                password  = password,
                firstName = firstName,
                lastName  = lastName,
            )
        )
        // Auto-login: fetch a token by logging in immediately after register
        val response = authAPI.login(LoginRequestDto(email, password))
        val user = response.user.toDomain()
        session.saveSession(user, response.token)
        user
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        authAPI.logout()
        session.clearSession()
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        authAPI.getCurrentUser().toDomain()
    }
}
