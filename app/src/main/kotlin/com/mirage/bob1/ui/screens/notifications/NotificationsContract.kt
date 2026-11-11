package com.mirage.bob1.ui.screens.notifications

import androidx.compose.foundation.lazy.items
import com.mirage.bob1.domain.model.AppNotification

data class NotificationsState(val items: List<AppNotification> = emptyList(), val refreshing: Boolean = false) {
    val hasUnread get() = items.any { !it.isRead }
}

/** What [NotificationsContent] can ask for; keeps the content stateless and previewable. */
data class NotificationsActions(
    val read: (AppNotification) -> Unit,
    val readAll: () -> Unit,
    val refresh: () -> Unit,
    val back: () -> Unit,
)
