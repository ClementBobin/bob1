package com.bob1.app.domain.repository

import com.bob1.app.domain.model.AppNotification

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<AppNotification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun getUnreadCount(): Result<Int>
}
