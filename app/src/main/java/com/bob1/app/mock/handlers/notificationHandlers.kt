package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.MessageResponseDto
import com.bob1.app.data.dto.NotificationDto
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod

private val mutableNotifications = BasketballMockData.notifications

val notificationHandlers: List<MockHandler> = listOf(

    // GET /notifications — all non-expired notifications
    MockHandler(HttpMethod.Get, "/notifications") { _, _ ->
        mutableNotifications.sortedWith(compareBy<NotificationDto>({ it.isRead }, { it.createdAt }).reversed())
    },

    // GET /notifications/startup — IsShowAtStart=true, recursive or unread
    MockHandler(HttpMethod.Get, "/notifications/startup") { _, _ ->
        mutableNotifications.filter { n ->
            n.isShowAtStart && (n.isRecursif || !n.isRead)
        }
    },

    // GET /notifications/unread-count — plain Int (not a map)
    MockHandler(HttpMethod.Get, "/notifications/unread-count") { _, _ ->
        mutableNotifications.count { !it.isRead }
    },

    // POST /notifications/:id/read
    MockHandler(HttpMethod.Post, "/notifications/:id/read") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableNotifications.indexOfFirst { it.id == id }
        if (idx >= 0) {
            val n = mutableNotifications[idx]
            // Recursive notifications are never permanently marked read
            if (!n.isRecursif) mutableNotifications[idx] = n.copy(isRead = true)
        }
        MessageResponseDto("Notification marquée comme lue.")
    },

    // POST /notifications/read-all — skips recursive
    MockHandler(HttpMethod.Post, "/notifications/read-all") { _, _ ->
        mutableNotifications.indices.forEach { i ->
            val n = mutableNotifications[i]
            if (!n.isRecursif) mutableNotifications[i] = n.copy(isRead = true)
        }
        MessageResponseDto("Toutes les notifications marquées comme lues.")
    },
)
