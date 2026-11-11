package com.mirage.bob1.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.NotificationBell
import com.mirage.bob1.ui.components.calendar.CalendarDayPanel
import com.mirage.bob1.ui.components.calendar.CalendarFilters
import com.mirage.bob1.ui.components.calendar.CalendarLegend
import com.mirage.bob1.ui.components.calendar.CalendarMonthGrid
import com.mirage.bob1.ui.components.calendar.CalendarMonthHeader
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen

@Composable
fun CalendarScreen(navController: NavController, viewModel: CalendarViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, event -> handle(event) }) { state, vm ->
        CalendarContent(
            state = state,
            actions = CalendarActions(
                changeMonth = vm::changeMonth,
                filterChild = vm::filterChild,
                filterDivision = vm::filterDivision,
                openMatch = vm::openMatch,
                openNotifications = vm::openNotifications,
                refresh = vm::refresh,
                selectDate = vm::selectDate,
            ),
        )
    }
}

@Composable
fun CalendarContent(state: CalendarState, actions: CalendarActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(actions = { NotificationBell(state.unread, actions.openNotifications) })
        PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = actions.refresh, modifier = Modifier.fillMaxSize()) {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                item {
                    Column {
                        if (state.firstName.isNotBlank()) Text("Bonjour ${state.firstName},", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Le calendrier du club", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    }
                }
                item { CalendarMonthHeader(state.month, onPrev = { actions.changeMonth(-1) }, onNext = { actions.changeMonth(1) }) }
                item { CalendarFilters(state, actions) }
                item {
                    CalendarMonthGrid(
                        month = state.month, matchesByDate = state.matchesByDate, committed = state.committedMatchIds,
                        selected = state.selectedDate, onSelect = actions.selectDate,
                    )
                }
                item { CalendarLegend() }
                item { CalendarDayPanel(state.selectedDate, state.matchesByDate, state.committedMatchIds, actions.openMatch) }
            }
        }
    }
}
