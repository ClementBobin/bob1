package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.MessageResponseDto
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod

private val mutableNotifications = BasketballMockData.notifications.toMutableList()

val notificationHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/notifications") { _, _ ->
        mutableNotifications.sortedBy { it.isRead }
    },

    MockHandler(HttpMethod.Get, "/notifications/unread-count") { _, _ ->
        mapOf("count" to mutableNotifications.count { !it.isRead })
    },

    MockHandler(HttpMethod.Post, "/notifications/:id/read") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableNotifications.indexOfFirst { it.id == id }
        if (idx >= 0) mutableNotifications[idx] = mutableNotifications[idx].copy(isRead = true)
        MessageResponseDto("Notification marquée comme lue.")
    },
)
