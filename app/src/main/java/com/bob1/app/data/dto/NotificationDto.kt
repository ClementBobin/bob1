package com.bob1.app.data.dto

import com.bob1.app.domain.model.AppNotification
import kotlinx.serialization.Serializable

enum class NotificationType { J15_REMINDER, J4_REMINDER, EMERGENCY, GENERAL }

@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val matchId: String? = null,
    val timestampIso: String,
    val isRead: Boolean = false,
) {
    fun toDomain() = AppNotification(
        id = id,
        type = NotificationType.valueOf(type),
        title = title,
        body = body,
        matchId = matchId,
        timestampIso = timestampIso,
        isRead = isRead,
    )

    companion object {
        fun fromDomain(n: AppNotification) = NotificationDto(
            id = n.id,
            type = n.type.name,
            title = n.title,
            body = n.body,
            matchId = n.matchId,
            timestampIso = n.timestampIso,
            isRead = n.isRead
        )
    }
}