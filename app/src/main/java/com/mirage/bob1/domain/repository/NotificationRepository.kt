package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.AppNotification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<AppNotification>>
    suspend fun getStartupNotifications(): Result<List<AppNotification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
    suspend fun getUnreadCount(): Result<Int>
}
