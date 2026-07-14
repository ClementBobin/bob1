package com.bob1.app.data.remote

import com.bob1.app.data.dto.NotificationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

internal class NotificationAPI(private val client: HttpClient) {

    suspend fun getNotifications(): List<NotificationDto> =
        client.get("/api/notifications").body()

    /** Notifications shown on startup (IsShowAtStart=true, not expired, recursive or unread). */
    suspend fun getStartupNotifications(): List<NotificationDto> =
        client.get("/api/notifications/startup").body()

    suspend fun markAsRead(notificationId: String): HttpResponse =
        client.post("/api/notifications/$notificationId/read")

    suspend fun markAllRead(): HttpResponse =
        client.post("/api/notifications/read-all")

    /** API returns a plain Int, not a map. */
    suspend fun getUnreadCount(): Int =
        client.get("/api/notifications/unread-count").body()
}
