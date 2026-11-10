package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.local.*
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.*

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val api: Bob1Api,
    private val local: NotificationLocalDataSource,
) : NotificationRepository {
    override fun observe() = local.observe()
    override fun observeUnreadCount() = local.observeUnreadCount()

    override suspend fun refresh(): Result<Unit> = apiCall {
        local.replace(api.notifications(NotificationQueryDto(take = 100)).map { it.toDomain() })
    }

    override suspend fun markRead(id: String): Result<Unit> = apiCall { api.markRead(id); local.markRead(id) }
    override suspend fun markAllRead(): Result<Unit> = apiCall { api.markAllRead(); local.markAllRead() }
}
