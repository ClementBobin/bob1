package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.PushAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.PushRepository
import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.capacity.push.PushMessaging

@Singleton
internal class PushRepositoryImpl @Inject constructor(
    private val pushAPI: PushAPI,
    private val pushMessaging: PushMessaging,
) : PushRepository {

    override suspend fun registerCurrentToken(): Result<Unit> =
        pushMessaging.currentToken()?.let { registerToken(it) } ?: Result.success(Unit)

    override suspend fun registerToken(token: String): Result<Unit> = apiCall { pushAPI.registerToken(token) }
}
