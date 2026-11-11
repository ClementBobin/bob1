package com.mirage.bob1.ui.screens.notifications

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.EmptyBox
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.core.formatShort
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onEvent = { _, _, e -> handle(e) },
    ) { state, vm ->
        NotificationsContent(
            state = state,
            actions = NotificationsActions(
                read = vm::read,
                readAll = vm::readAll,
                refresh = vm::refresh,
                back = { navController.popBackStack() },
            ),
        )
    }
}

@Composable
fun NotificationsContent(state: NotificationsState, actions: NotificationsActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Notifications", onBack = { actions.back() }, actions = {
            if (state.hasUnread) KButton("Tout lire", actions.readAll, variant = KButtonVariant.Ghost, size = KButtonSize.Sm)
        })
        PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = actions.refresh, modifier = Modifier.fillMaxSize()) {
            if (state.items.isEmpty() && !state.refreshing) {
                EmptyBox(Icons.Outlined.Notifications, "Rien de nouveau", "Les annonces du club s'afficheront ici.")
            } else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.items, key = { it.id }) { n ->
                    SectionCard(title = n.title, modifier = Modifier.clickable { actions.read(n) }, trailing = {
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
