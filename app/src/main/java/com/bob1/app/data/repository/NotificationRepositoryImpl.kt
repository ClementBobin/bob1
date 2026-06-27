package com.bob1.app.data.repository

import com.bob1.app.data.remote.NotificationAPI
import com.bob1.app.domain.model.AppNotification
import com.bob1.app.domain.repository.NotificationRepository

/**
 * Implémentation de [NotificationRepository] utilisant [NotificationAPI]
 */
internal class NotificationRepositoryImpl(
    private val notificationAPI: NotificationAPI
) : NotificationRepository {

    override suspend fun getNotifications(): Result<List<AppNotification>> = runCatching {
        notificationAPI.getNotifications().map { it.toDomain() }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = runCatching {
        notificationAPI.markAsRead(notificationId)
    }

    override suspend fun getUnreadCount(): Result<Int> = runCatching {
        notificationAPI.getUnreadCount()
    }
}