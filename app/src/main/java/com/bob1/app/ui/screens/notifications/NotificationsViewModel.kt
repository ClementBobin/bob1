package com.bob1.app.ui.screens.notifications

import android.app.Application
import com.bob1.app.domain.model.AppNotification
import com.bob1.app.domain.repository.NotificationRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

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

class NotificationsViewModel(
    application: Application,
) : KViewModel<NotificationsContracts.UiState>(NotificationsContracts.UiState(), application) {

    private val repo: NotificationRepository by inject()

    init { loadNotifications() }

    fun loadNotifications() {
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source   = { repo.getNotifications().getOrThrow() },
            onResult = {
                onSuccess { notifications -> updateState { copy(isLoading = false, notifications = notifications) } }
                onFailure { e -> updateState { copy(isLoading = false, error = e.message) } }
            }
        )
    }

    fun onNotificationTapped(notification: AppNotification) {
        fetchData(
            source   = { repo.markAsRead(notification.id) },
            onResult = {
                onSuccess {
                    updateState {
                        copy(notifications = notifications.map {
                            if (it.id == notification.id) it.copy(isRead = true) else it
                        })
                    }
                    notification.matchId?.let { matchId ->
                        sendEvent(NotificationsContracts.UiEvent.NavigateToMatch(matchId))
                    }
                }
            }
        )
    }
}