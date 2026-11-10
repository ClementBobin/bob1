package org.bob1.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bob1.app.domain.model.*

interface NotificationRepository {
    fun observe(): Flow<List<AppNotification>>
    fun observeUnreadCount(): Flow<Int>
    suspend fun refresh(): Result<Unit>
    suspend fun markRead(id: String): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
}
