package com.mirage.bob1.ui.screens.notifications

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.mirage.bob1.domain.model.AppNotification
import com.mirage.bob1.domain.usecase.*
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatShort
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler

data class NotificationsState(val items: List<AppNotification> = emptyList(), val refreshing: Boolean = false) {
    val hasUnread get() = items.any { !it.isRead }
}

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

@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onBack = { _, _ -> navController.popBackStack() }, onEvent = { _, _, e -> handle(e) },
    ) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Notifications", onBack = { navController.popBackStack() }, actions = {
                if (state.hasUnread) KButton("Tout lire", vm::readAll, variant = KButtonVariant.Ghost, size = KButtonSize.Sm)
            })
            PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = vm::refresh, modifier = Modifier.fillMaxSize()) {
                if (state.items.isEmpty() && !state.refreshing) {
                    EmptyBox(Icons.Outlined.Notifications, "Rien de nouveau", "Les annonces du club s'afficheront ici.")
                } else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.items, key = { it.id }) { n ->
                        SectionCard(title = n.title, modifier = Modifier.clickable { vm.read(n) }, trailing = {
                            if (!n.isRead) Box(Modifier.size(9.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        }) {
                            Text(n.body, color = MaterialTheme.colorScheme.onSurface)
                            Text(n.createdAt.formatShort(), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
