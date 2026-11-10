package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.local.*
import com.mirage.bob1.data.remote.AuthApi
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.*
import com.mirage.bob1.domain.repository.*
import org.bob1.capacity.push.PushMessaging
import org.bob1.capacity.session.SecureSession

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val api: Bob1Api,
    private val session: SecureSession,
    private val cache: LocalCache,
    private val pushMessaging: PushMessaging,
) : AuthRepository {

    override val isAuthenticated: Flow<Boolean?> =
        combine(session.isReady, session.accessTokenFlow) { ready, token -> if (!ready) null else token != null }
            .distinctUntilChanged()

    override suspend fun login(email: String, password: String): Result<User> = apiCall {
        val response = authApi.login(LoginRequestDto(email, password))
        session.save(response.accessToken, response.refreshToken)
        response.user.toDomain()
    }

    override suspend fun register(form: Registration): Result<User> = apiCall {
        val response = authApi.register(RegisterRequestDto.fromDomain(form))
        session.save(response.accessToken, response.refreshToken) // signed in right away: AppRoot swaps to the main graph
        response.user.toDomain()
    }

    override suspend fun me(): Result<User> = apiCall { api.me().toDomain() }

    override suspend fun logout() {
        // Best effort: revoke the refresh token and unregister this device, then wipe everything local.
        session.refreshToken?.let { token -> apiCall { api.logout(RefreshRequestDto(token)) } }
        pushMessaging.currentToken()?.let { token -> apiCall { api.unregisterPushToken(PushTokenDto(token)) } }
        session.clear()
        cache.clear()
    }
}
