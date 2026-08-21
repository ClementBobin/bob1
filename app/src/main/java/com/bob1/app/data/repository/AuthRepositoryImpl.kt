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

    /**
     * Saves the JWT from the login response, then fetches the full user
     * via GET /api/auth/me (which requires the token to be set first).
     */
    private suspend fun saveTokenAndFetchUser(token: String): User {
        // Save token first so the /me request is authenticated
        session.saveToken(token)
        val user = authAPI.getCurrentUser().toDomain()
        // Session expires when the app is closed (expiresTime = 0 = never expire via time,
        // but clearSession() is called in Application.onTrimMemory TRIM_MEMORY_UI_HIDDEN)
        session.saveSession(user, token, expiresTime = 0L)
        return user
    }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = authAPI.login(LoginRequestDto(email, password))
        saveTokenAndFetchUser(response.token)
    }

    /**
     * Exchanges the stored server-issued biometric token for a fresh session JWT,
     * then fetches the user profile via GET /api/auth/me.
     */
    override suspend fun loginWithBiometric(): Result<User> = runCatching {
        val bioToken = session.getBiometricToken()
            ?: error("Aucun token biométrique enregistré. Activez la biométrie dans votre profil.")
        val response = authAPI.biometricLogin(bioToken)
        saveTokenAndFetchUser(response.token)
    }

    /**
     * Calls the server to generate a long-lived biometric token, then stores it
     * encrypted on-device. Must be called while a valid session JWT exists.
     */
    override suspend fun generateAndSaveBiometricToken(): Result<Unit> = runCatching {
        val response = authAPI.generateBiometricToken()
        session.saveBiometricToken(response.token)
    }

    /**
     * Revokes the biometric token server-side and clears the local copy.
     */
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
        val userDto = authAPI.register(
            RegisterRequestDto(
                email     = email,
                password  = password,
                firstName = firstName,
                lastName  = lastName,
            )
        )
        // Auto-login to get a session token right after registering
        val response = authAPI.login(LoginRequestDto(email, password))
        saveTokenAndFetchUser(response.token)
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        authAPI.logout()
        session.clearSession()
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        authAPI.getCurrentUser().toDomain()
    }
}
