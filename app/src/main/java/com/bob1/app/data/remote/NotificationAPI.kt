package com.bob1.app.data.remote

import com.bob1.app.data.dto.NotificationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

/**
 * Passerelle vers les endpoints de notifications de l'API (/api/notifications)
 */
internal class NotificationAPI(private val client: HttpClient) {

    suspend fun getNotifications(): List<NotificationDto> =
        client.get("/api/notifications").body()

    suspend fun markAsRead(notificationId: String): HttpResponse =
        client.post("/api/notifications/$notificationId/read")

    suspend fun getUnreadCount(): Int =
        client.get("/api/notifications/unread-count").body<Map<String, Int>>()["count"] ?: 0
}