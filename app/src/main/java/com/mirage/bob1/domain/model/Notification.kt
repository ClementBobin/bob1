package com.mirage.bob1.domain.model

import com.mirage.bob1.data.dto.NotificationType

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val matchId: String?,
    val timestampIso: String,
    val isRead: Boolean = false,
    val isRecursif: Boolean = false,
    val isShowAtStart: Boolean = false,
    val expiresAt: String? = null,
)
