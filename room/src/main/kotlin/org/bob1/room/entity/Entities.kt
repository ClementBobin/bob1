package org.bob1.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Cached calendar entry. Dates are epoch millis (UTC). */
@Entity(tableName = "matches", indices = [Index("startsAtUtc"), Index("divisionId")])
data class MatchEntity(
    @PrimaryKey val id: String,
    val title: String,
    val divisionId: String,
    val divisionName: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val venueId: String,
    val startsAtUtc: Long,
    val status: String,
)

/** "Mes engagements": one row per (type, match). */
@Entity(tableName = "commitments", primaryKeys = ["type", "matchId"], indices = [Index("startsAtUtc")])
data class CommitmentEntity(
    val type: String,
    val matchId: String,
    val matchTitle: String,
    val startsAtUtc: Long,
    val label: String,
)

@Entity(tableName = "notifications", indices = [Index("createdAtUtc")])
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val body: String,
    val dataJson: String?,
    val createdAtUtc: Long,
    val isRead: Boolean,
)
