package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.*
import org.bob1.capacity.push.PushMessaging

@Singleton
class PushRepositoryImpl @Inject constructor(
    private val api: Bob1Api,
    private val pushMessaging: PushMessaging,
) : PushRepository {
    override suspend fun registerCurrentToken(): Result<Unit> =
        pushMessaging.currentToken()?.let { registerToken(it) } ?: Result.success(Unit)

    override suspend fun registerToken(token: String) = apiCall { api.registerPushToken(PushTokenDto(token)) }
}
