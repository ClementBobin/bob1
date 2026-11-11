package com.mirage.bob1.ui.screens.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.EmptyBox
import com.mirage.bob1.ui.components.ErrorBox
import com.mirage.bob1.ui.components.LoadingBox
import com.mirage.bob1.ui.components.NotificationBell
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.staff.StaffMatchLine
import com.mirage.bob1.ui.core.formatLong
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen

@Composable
fun StaffCalendarScreen(navController: NavController, viewModel: StaffCalendarViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        StaffCalendarContent(
            state = state,
            actions = StaffCalendarActions(
                filter = vm::filter,
                load = vm::load,
                open = vm::open,
                openNotifications = vm::openNotifications,
                pull = vm::pull,
            ),
        )
    }
}

@Composable
fun StaffCalendarContent(state: StaffCalendarState, actions: StaffCalendarActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(actions = { NotificationBell(state.unread, actions.openNotifications) })
        when {
            state.loading -> LoadingBox()
            state.error != null -> ErrorBox(state.error, onRetry = { actions.load() })
            else -> PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = { actions.pull() }, modifier = Modifier.fillMaxSize()) {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Column {
                            Text("Calendrier des officiels", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                            Text(
                                if (state.upcomingAssignments == 1) "1 affectation à venir" else "${state.upcomingAssignments} affectations à venir",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item { FilterChip(selected = state.division == null, onClick = { actions.filter(null) }, label = { Text("Tous") }) }
                            items(state.divisions) { d -> FilterChip(selected = state.division == d, onClick = { actions.filter(d) }, label = { Text(d) }) }
                        }
                    }
                    if (state.visible.isEmpty()) {
                        item { EmptyBox(Icons.Outlined.EventBusy, "Aucun poste à pourvoir", "Aucun match avec des postes d'officiels dans les 30 prochains jours.") }
                    }
                    state.byDay.forEach { (day, list) ->
                        item(key = day.toString()) {
                            SectionCard(title = day.formatLong()) { list.forEach { m -> StaffMatchLine(m) { actions.open(m) } } }
                        }
                    }
                }
            }
        }
    }
}
