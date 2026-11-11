package com.mirage.bob1.ui.screens.notifications

import android.app.Application
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.model.AppNotification
import com.mirage.bob1.domain.usecase.MarkAllNotificationsReadUseCase
import com.mirage.bob1.domain.usecase.MarkNotificationReadUseCase
import com.mirage.bob1.domain.usecase.ObserveNotificationsUseCase
import com.mirage.bob1.domain.usecase.RefreshNotificationsUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    application: Application,
    observe: ObserveNotificationsUseCase,
    private val refreshUseCase: RefreshNotificationsUseCase,
    private val markRead: MarkNotificationReadUseCase,
    private val markAllRead: MarkAllNotificationsReadUseCase,
) : Bob1ViewModel<NotificationsState>(NotificationsState(), application) {

    init {
        viewModelScope.launch { observe().collect { list -> updateState { copy(items = list) } } }
        refresh()
    }

    fun refresh() {
        updateState { copy(refreshing = true) }
        call({ refreshUseCase() }, onError = { updateState { copy(refreshing = false) }; sendError(it) }) { updateState { copy(refreshing = false) } }
    }

    fun read(n: AppNotification) { if (!n.isRead) call({ markRead(n.id) }, onError = ::sendError) }
    fun readAll() = call({ markAllRead() }, onError = ::sendError)
}
