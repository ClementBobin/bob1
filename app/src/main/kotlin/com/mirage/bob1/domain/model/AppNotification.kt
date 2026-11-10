package com.mirage.bob1.domain.model

import java.time.Instant

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Instant,
    val isRead: Boolean,
)
