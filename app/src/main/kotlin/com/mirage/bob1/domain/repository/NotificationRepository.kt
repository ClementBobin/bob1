package com.mirage.bob1.domain.repository

import kotlinx.coroutines.flow.Flow
import com.mirage.bob1.domain.model.*

interface NotificationRepository {
    fun observe(): Flow<List<AppNotification>>
    fun observeUnreadCount(): Flow<Int>
    suspend fun refresh(): Result<Unit>
    suspend fun markRead(id: String): Result<Unit>
    suspend fun markAllRead(): Result<Unit>
}
