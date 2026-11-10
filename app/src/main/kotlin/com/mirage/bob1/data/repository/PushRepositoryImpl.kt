package org.bob1.app.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.dto.*
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.repository.*
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
