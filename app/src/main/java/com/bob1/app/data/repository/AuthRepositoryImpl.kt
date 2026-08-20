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
        val user = getCurrentUser()
        session.saveSession(user, response.token, response.expiresTime)
        user
    }

    /**
     * Exchanges the stored server-issued biometric token for a fresh session JWT.
     * The hardware biometric prompt has already succeeded before this is called —
     * it acts as the local authentication gate.
     */
    override suspend fun loginWithBiometric(): Result<User> = runCatching {
        val bioToken = session.getBiometricToken()
            ?: error("Aucun token biométrique enregistré.")
        val response = authAPI.biometricLogin(bioToken)
        val user = getCurrentUser()
        session.saveSession(user, response.token, response.expiresTime)
        user
}

    /**
     * Calls the server to generate a long-lived biometric token, then stores it
     * encrypted on-device. Must be called while a valid session JWT exists
     * (i.e. right after a successful password login or from the Profile page).
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
        authAPI.register(
            RegisterRequestDto(
                email     = email,
                password  = password,
                firstName = firstName,
                lastName  = lastName,
            )
        )
        // Auto-login to get a session token right after registering
        val response = login(LoginRequestDto(email, password))
        val user = getCurrentUser()
        session.saveSession(user, response.token, response.expiresTime)
        user
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        // Best-effort: revoke biometric token server-side on logout
        if (session.hasBiometricToken()) {
            runCatching { authAPI.removeBiometricToken() }
            session.clearBiometricToken()
        }
        authAPI.logout()
        session.clearSession()
    }

    override suspend fun getCurrentUser(): Result<User> = runCatching {
        authAPI.getCurrentUser().toDomain()
    }
}