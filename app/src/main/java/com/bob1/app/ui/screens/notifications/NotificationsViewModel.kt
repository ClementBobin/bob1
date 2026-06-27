package com.bob1.app.ui.screens.notifications

import android.app.Application
import com.bob1.app.domain.model.AppNotification
import com.bob1.app.domain.repository.NotificationRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

// ── State ─────────────────────────────────────────────────────────────────────
object NotificationsContracts {
    data class UiState(
        val isLoading: Boolean = false,
        val notifications: List<AppNotification> = emptyList(),
        val error: String? = null,
    )

    sealed interface UiEvent {
        data class NavigateToMatch(val matchId: String) : UiEvent
    }
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class NotificationsViewModel(
    application: Application,
) : KViewModel<NotificationsContracts.UiState>(NotificationsContracts.UiState(), application) {

    private val repo: NotificationRepository by inject()

    init { loadNotifications() }

    fun loadNotifications() {
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source = { repo.getNotifications().getOrThrow() },
            onResult = { result ->
                result
                    .onSuccess { notifications ->
                        updateState { copy(isLoading = false, notifications = notifications) }
                    }
                    .onFailure { error ->
                        updateState { copy(isLoading = false, error = error.message) }
                    }
            }
        )
    }

    fun onNotificationTapped(notification: AppNotification) {
        // Mark as read
        fetchData(
            source = { repo.markAsRead(notification.id) },
            onResult = { result ->
                result.onSuccess {
                    updateState {
                        copy(notifications = notifications.map {
                            if (it.id == notification.id) it.copy(isRead = true) else it
                        })
                    }
                    // Navigate to match if applicable
                    notification.matchId?.let { matchId ->
                        sendEvent(NotificationsContracts.UiEvent.NavigateToMatch(matchId))
                    }
                }
            }
        )
    }
}