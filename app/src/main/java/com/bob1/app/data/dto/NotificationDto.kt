package com.bob1.app.data.dto

import com.bob1.app.domain.model.AppNotification
import kotlinx.serialization.Serializable

// API sends Pascal case: "J15Reminder", "J4Reminder", "Emergency", "General"
enum class NotificationType {
    J15_REMINDER, J4_REMINDER, EMERGENCY, GENERAL;

    companion object {
        fun fromApiString(value: String): NotificationType = when (value.lowercase()) {
            "j15reminder" -> J15_REMINDER
            "j4reminder"  -> J4_REMINDER
            "emergency"   -> EMERGENCY
            "general"     -> GENERAL
            else          -> GENERAL
        }
    }
}

@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val isRead: Boolean = false,
    val isRecursif: Boolean = false,
    val isShowAtStart: Boolean = false,
    val expiresAt: String? = null,
    val createdAt: String,          // API field name
    val matchId: String? = null,
    val createdByAdminId: String? = null,
) {
    fun toDomain() = AppNotification(
        id             = id,
        type           = NotificationType.fromApiString(type),
        title          = title,
        body           = body,
        matchId        = matchId,
        timestampIso   = createdAt,
        isRead         = isRead,
        isRecursif     = isRecursif,
        isShowAtStart  = isShowAtStart,
        expiresAt      = expiresAt,
    )
}
