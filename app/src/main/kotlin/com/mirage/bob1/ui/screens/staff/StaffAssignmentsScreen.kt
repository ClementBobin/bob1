package com.mirage.bob1.ui.screens.staff

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
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
import com.mirage.bob1.ui.components.ErrorBox
import com.mirage.bob1.ui.components.LoadingBox
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.components.staff.StatCard
import com.mirage.bob1.ui.core.formatDayTime
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen

@Composable
fun StaffAssignmentsScreen(navController: NavController, viewModel: StaffAssignmentsViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        StaffAssignmentsContent(
            state = state,
            actions = StaffAssignmentsActions(
                load = vm::load,
                open = vm::open,
                pull = vm::pull,
            ),
        )
    }
}

@Composable
fun StaffAssignmentsContent(state: StaffAssignmentsState, actions: StaffAssignmentsActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Mes affectations")
        when {
            state.loading -> LoadingBox()
            state.error != null -> ErrorBox(state.error, onRetry = { actions.load() })
            else -> PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = { actions.pull() }, modifier = Modifier.fillMaxSize()) {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            StatCard("À venir", state.items.size.toString(), Modifier.weight(1f))
                            StatCard("Points saison", state.seasonPoints?.toString() ?: "–", Modifier.weight(1f))
                        }
                    }
                    if (state.items.isEmpty()) {
                        item { EmptyBox(Icons.Outlined.Shield, "Aucune affectation", "Inscrivez-vous à un poste depuis le calendrier des officiels.") }
                    }
                    items(state.items, key = { it.slotId }) { a ->
                        SectionCard(title = a.matchTitle, modifier = Modifier.clickable { actions.open(a) }, trailing = { StatusBadge(a.role, Tone.Success) }) {
                            Text(a.startsAt.formatDayTime(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
