package com.mirage.bob1.data.repository

import com.mirage.bob1.data.local.NotificationLocalDataSource
import com.mirage.bob1.data.remote.NotificationAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.AppNotification
import com.mirage.bob1.domain.repository.NotificationRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
internal class NotificationRepositoryImpl @Inject constructor(
    private val notificationAPI: NotificationAPI,
    private val local: NotificationLocalDataSource,
) : NotificationRepository {

    override fun observe(): Flow<List<AppNotification>> = local.observe()
    override fun observeUnreadCount(): Flow<Int> = local.observeUnreadCount()

    override suspend fun refresh(): Result<Unit> = apiCall {
        local.replace(notificationAPI.getNotifications().map { it.toDomain() })
    }

    override suspend fun markRead(id: String): Result<Unit> = apiCall {
        notificationAPI.markAsRead(id)
        local.markRead(id)
    }

    override suspend fun markAllRead(): Result<Unit> = apiCall {
        notificationAPI.markAllRead()
        local.markAllRead()
    }
}
