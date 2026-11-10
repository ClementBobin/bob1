package org.bob1.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.bob1.room.entity.CommitmentEntity
import org.bob1.room.entity.MatchEntity
import org.bob1.room.entity.NotificationEntity

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE startsAtUtc BETWEEN :from AND :to ORDER BY startsAtUtc")
    fun observeBetween(from: Long, to: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun get(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<MatchEntity>)

    @Query("DELETE FROM matches WHERE startsAtUtc BETWEEN :from AND :to")
    suspend fun deleteBetween(from: Long, to: Long)

    /** Server is the source of truth for the window: what disappeared there disappears here. */
    @Transaction
    suspend fun replaceBetween(from: Long, to: Long, items: List<MatchEntity>) {
        deleteBetween(from, to)
        upsertAll(items)
    }

    @Query("DELETE FROM matches")
    suspend fun clear()
}

@Dao
interface CommitmentDao {
    @Query("SELECT * FROM commitments ORDER BY startsAtUtc")
    fun observeAll(): Flow<List<CommitmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CommitmentEntity>)

    @Query("DELETE FROM commitments")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<CommitmentEntity>) {
        clear()
        upsertAll(items)
    }
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAtUtc DESC")
    fun observeAll(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun observeUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllRead()

    @Query("DELETE FROM notifications")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(items: List<NotificationEntity>) {
        clear()
        upsertAll(items)
    }
}
