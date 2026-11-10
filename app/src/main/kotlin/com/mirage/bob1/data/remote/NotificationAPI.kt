package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.NotificationDto
import com.mirage.bob1.data.dto.NotificationQueryDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import javax.inject.Inject

internal class NotificationAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /notifications */
    suspend fun getNotifications(): List<NotificationDto> =
        client.query("notifications", NotificationQueryDto(take = 100)).body()

    suspend fun markAsRead(notificationId: String) {
        client.post("notifications/$notificationId/read").ok()
    }

    suspend fun markAllRead() {
        client.post("notifications/read-all").ok()
    }
}
