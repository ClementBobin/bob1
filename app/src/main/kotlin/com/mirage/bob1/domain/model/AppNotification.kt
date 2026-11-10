package org.bob1.app.domain.model

import java.time.Instant

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Instant,
    val isRead: Boolean,
)
