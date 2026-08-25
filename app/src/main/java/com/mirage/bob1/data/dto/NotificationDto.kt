package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.AppNotification
import kotlinx.serialization.Serializable

// API sends NotificationType as integer: 0=J15Reminder, 1=J4Reminder, 2=Emergency, 3=General
// fromApiString kept for mock compatibility.
enum class NotificationType {
    J15_REMINDER, J4_REMINDER, EMERGENCY, GENERAL;

    /** Serialise vers l'entier attendu par l'API admin create request. */
    fun toApiInt(): Int = when (this) {
        J15_REMINDER -> 0
        J4_REMINDER  -> 1
        EMERGENCY    -> 2
        GENERAL      -> 3
    }

    companion object {
        fun fromApiInt(value: Int): NotificationType = when (value) {
            0    -> J15_REMINDER
            1    -> J4_REMINDER
            2    -> EMERGENCY
            3    -> GENERAL
            else -> GENERAL
        }

        /** Kept for mock compatibility (legacy string format). */
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
    val type: Int,  // API now sends NotificationType as integer
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
        type           = NotificationType.fromApiInt(type),
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