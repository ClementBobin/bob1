package org.bob1.app.data.local

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bob1.app.domain.model.*
import org.bob1.room.dao.NotificationDao
import org.bob1.room.entity.NotificationEntity

private fun NotificationEntity.toDomain() = AppNotification(id, title, body, Instant.ofEpochMilli(createdAtUtc), isRead)

private fun AppNotification.toEntity() = NotificationEntity(id, title, body, null, createdAt.toEpochMilli(), isRead)

@Singleton
class NotificationLocalDataSource @Inject constructor(private val dao: NotificationDao) {
    fun observe(): Flow<List<AppNotification>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    fun observeUnreadCount(): Flow<Int> = dao.observeUnreadCount()
    suspend fun replace(items: List<AppNotification>) = dao.replaceAll(items.map { it.toEntity() })
    suspend fun markRead(id: String) = dao.markRead(id)
    suspend fun markAllRead() = dao.markAllRead()
}
