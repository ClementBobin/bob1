package com.bob1.app.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.domain.model.AppNotification
import com.bob1.app.ui.core.components.ui.AppBottomBar
import com.bob1.app.ui.core.components.ui.NotificationCard
import dev.kindling.compose.KScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavController
) {
    KScreen(
        viewModel = viewModel<NotificationsViewModel>(),
        navController = navController
    ) { state, viewModel ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            },
            bottomBar = { AppBottomBar(navController) }
        ) { paddingValues ->
            NotificationsContent(
                state = state,
                paddingValues = paddingValues,
                onNotificationTapped = viewModel::onNotificationTapped,
                onRetry = viewModel::loadNotifications
            )
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun NotificationsContent(
    state: NotificationsContracts.UiState,
    paddingValues: PaddingValues,
    onNotificationTapped: (AppNotification) -> Unit = {},
    onRetry: () -> Unit = {}
) {
    when {
        state.isLoading -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRetry) {
                        Text("Réessayer")
                    }
                }
            }
        }

        state.notifications.isEmpty() -> {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Aucune notification",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        else -> {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp + paddingValues.calculateTopPadding(),
                    bottom = 16.dp + paddingValues.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onTap = { onNotificationTapped(notification) }
                    )
                }
            }
        }
    }
}