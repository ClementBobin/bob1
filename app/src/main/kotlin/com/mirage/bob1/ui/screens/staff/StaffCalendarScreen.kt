package com.mirage.bob1.ui.screens.staff

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
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
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.mirage.bob1.domain.model.OpenStaffMatch
import com.mirage.bob1.domain.usecase.GetAssignmentsUseCase
import com.mirage.bob1.domain.usecase.GetOpenStaffMatchesUseCase
import com.mirage.bob1.domain.usecase.ObserveUnreadCountUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatLong
import com.mirage.bob1.ui.core.formatTime
import com.mirage.bob1.ui.core.navigation.NotificationsDestination
import com.mirage.bob1.ui.core.navigation.StaffMatchDestination
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import com.mirage.bob1.ui.core.toLocalDate
import com.mirage.bob1.ui.core.userMessage

data class StaffCalendarState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val matches: List<OpenStaffMatch> = emptyList(),
    val upcomingAssignments: Int = 0,
    val division: String? = null,
    val unread: Int = 0,
) {
    val divisions get() = matches.map { it.divisionName }.filter { it.isNotBlank() }.distinct().sorted()
    val visible get() = division?.let { d -> matches.filter { it.divisionName == d } } ?: matches
    val byDay: Map<LocalDate, List<OpenStaffMatch>> get() = visible.groupBy { it.startsAt.toLocalDate() }.toSortedMap()
}

@HiltViewModel
class StaffCalendarViewModel @Inject constructor(
    application: Application,
    private val getOpen: GetOpenStaffMatchesUseCase,
    private val getAssignments: GetAssignmentsUseCase,
    observeUnread: ObserveUnreadCountUseCase,
) : Bob1ViewModel<StaffCalendarState>(StaffCalendarState(), application) {

    init {
        viewModelScope.launch { observeUnread().collect { n -> updateState { copy(unread = n) } } }
        load()
    }

    fun load(pull: Boolean = false) {
        updateState { copy(loading = !pull && matches.isEmpty(), refreshing = pull, error = null) }
        call(
            block = { getOpen() },
            onError = { updateState { copy(loading = false, refreshing = false, error = if (matches.isEmpty()) it.userMessage() else null) }; sendError(it) },
        ) { list -> updateState { copy(loading = false, refreshing = false, matches = list) } }
        call({ getAssignments() }, onError = {}) { a -> updateState { copy(upcomingAssignments = a.size) } }
    }

    fun filter(division: String?) = updateState { copy(division = division) }
    fun open(m: OpenStaffMatch) = navigate(StaffMatchDestination(m.matchId))
    fun openNotifications() = navigate(NotificationsDestination)
}

@Composable
fun StaffCalendarScreen(navController: NavController, viewModel: StaffCalendarViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(actions = { NotificationBell(state.unread, vm::openNotifications) })
            when {
                state.loading -> LoadingBox()
                state.error != null -> ErrorBox(state.error, onRetry = { vm.load() })
                else -> PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = { vm.load(pull = true) }, modifier = Modifier.fillMaxSize()) {
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
                                item { FilterChip(selected = state.division == null, onClick = { vm.filter(null) }, label = { Text("Tous") }) }
                                items(state.divisions) { d -> FilterChip(selected = state.division == d, onClick = { vm.filter(d) }, label = { Text(d) }) }
                            }
                        }
                        if (state.visible.isEmpty()) {
                            item { EmptyBox(Icons.Outlined.EventBusy, "Aucun poste à pourvoir", "Aucun match avec des postes d'officiels dans les 30 prochains jours.") }
                        }
                        state.byDay.forEach { (day, list) ->
                            item(key = day.toString()) {
                                SectionCard(title = day.formatLong()) { list.forEach { m -> StaffMatchLine(m) { vm.open(m) } } }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffMatchLine(m: OpenStaffMatch, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(m.startsAt.formatTime(), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.width(44.dp))
        Column(Modifier.weight(1f)) {
            Text(m.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(m.divisionName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        when {
            m.mine -> StatusBadge("Inscrit", Tone.Joined)
            m.isFull -> StatusBadge("Complet", Tone.Danger)
            else -> StatusBadge("${m.missing} poste${if (m.missing > 1) "s" else ""} libre${if (m.missing > 1) "s" else ""}", Tone.Pending)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
