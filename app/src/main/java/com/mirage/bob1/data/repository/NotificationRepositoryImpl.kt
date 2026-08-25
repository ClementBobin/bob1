package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.NotificationAPI
import com.mirage.bob1.domain.model.AppNotification
import com.mirage.bob1.domain.repository.NotificationRepository

internal class NotificationRepositoryImpl(
    private val notificationAPI: NotificationAPI
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<AppNotification>> = runCatching {
        notificationAPI.getNotifications().map { it.toDomain() }
    }

    override suspend fun getStartupNotifications(): Result<List<AppNotification>> = runCatching {
        notificationAPI.getStartupNotifications().map { it.toDomain() }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = runCatching {
        notificationAPI.markAsRead(notificationId)
    }

    override suspend fun markAllRead(): Result<Unit> = runCatching {
        notificationAPI.markAllRead()
    }

    override suspend fun getUnreadCount(): Result<Int> = runCatching {
        notificationAPI.getUnreadCount()
    }
}
