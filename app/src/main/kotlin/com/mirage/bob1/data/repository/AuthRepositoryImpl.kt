package com.mirage.bob1.data.repository

import com.mirage.bob1.data.dto.RegisterRequestDto
import com.mirage.bob1.data.local.LocalCache
import com.mirage.bob1.data.remote.AccountAPI
import com.mirage.bob1.data.remote.AuthAPI
import com.mirage.bob1.data.remote.PushAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Registration
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import org.bob1.capacity.push.PushMessaging
import org.bob1.capacity.session.SecureSession

@Singleton
internal class AuthRepositoryImpl @Inject constructor(
    private val authAPI: AuthAPI,
    private val accountAPI: AccountAPI,
    private val pushAPI: PushAPI,
    private val session: SecureSession,
    private val cache: LocalCache,
    private val pushMessaging: PushMessaging,
) : AuthRepository {

    override val isAuthenticated: Flow<Boolean?> =
        combine(session.isReady, session.accessTokenFlow) { ready, token -> if (!ready) null else token != null }
            .distinctUntilChanged()

    override suspend fun login(email: String, password: String): Result<User> = apiCall {
        val response = authAPI.login(email, password)
        session.save(response.accessToken, response.refreshToken)
        response.user.toDomain()
    }

    override suspend fun register(form: Registration): Result<User> = apiCall {
        val response = authAPI.register(RegisterRequestDto.fromDomain(form))
        session.save(response.accessToken, response.refreshToken) // signed in right away: AppRoot swaps to the main graph
        response.user.toDomain()
    }

    override suspend fun me(): Result<User> = apiCall { accountAPI.getMe().toDomain() }

    override suspend fun logout() {
        // Best effort: revoke the refresh token and unregister this device, then wipe everything local.
        session.refreshToken?.let { token -> apiCall { accountAPI.logout(token) } }
        pushMessaging.currentToken()?.let { token -> apiCall { pushAPI.unregisterToken(token) } }
        session.clear()
        cache.clear()
    }
}
