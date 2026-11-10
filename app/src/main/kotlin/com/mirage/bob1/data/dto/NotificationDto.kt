package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val dataJson: String? = null,
    val createdAtUtc: String,
    val isRead: Boolean = false,
) {
    fun toDomain() = AppNotification(id, title, body, parseUtc(createdAtUtc), isRead)
}
