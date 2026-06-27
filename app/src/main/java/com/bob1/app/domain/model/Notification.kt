package com.bob1.app.domain.model

import com.bob1.app.data.dto.NotificationType

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val matchId: String?,
    val timestampIso: String,
    val isRead: Boolean = false,
)
