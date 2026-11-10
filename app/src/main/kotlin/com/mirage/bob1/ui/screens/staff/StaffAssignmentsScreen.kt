package com.mirage.bob1.ui.screens.staff

import android.app.Application
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.card.KCard
import dev.kindling.core.components.ui.card.KCardContent
import javax.inject.Inject
import com.mirage.bob1.domain.model.StaffAssignment
import com.mirage.bob1.domain.usecase.GetAssignmentsUseCase
import com.mirage.bob1.domain.usecase.GetStaffRankingUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatDayTime
import com.mirage.bob1.ui.core.navigation.StaffMatchDestination
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import com.mirage.bob1.ui.core.userMessage

data class StaffAssignmentsState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val items: List<StaffAssignment> = emptyList(),
    val seasonPoints: Int? = null,
)

@HiltViewModel
class StaffAssignmentsViewModel @Inject constructor(
    application: Application,
    private val getAssignments: GetAssignmentsUseCase,
    private val getStaffRanking: GetStaffRankingUseCase,
) : Bob1ViewModel<StaffAssignmentsState>(StaffAssignmentsState(), application) {

    init { load() }

    fun load(pull: Boolean = false) {
        updateState { copy(loading = !pull && items.isEmpty(), refreshing = pull, error = null) }
        call(
            block = { getAssignments() },
            onError = { updateState { copy(loading = false, refreshing = false, error = if (items.isEmpty()) it.userMessage() else null) }; sendError(it) },
        ) { list -> updateState { copy(loading = false, refreshing = false, items = list) } }
        call({ getStaffRanking() }, onError = {}) { r -> updateState { copy(seasonPoints = r.mine?.points) } }
    }

    fun open(a: StaffAssignment) = navigate(StaffMatchDestination(a.matchId))
}

@Composable
fun StaffAssignmentsScreen(navController: NavController, viewModel: StaffAssignmentsViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Mes affectations")
            when {
                state.loading -> LoadingBox()
                state.error != null -> ErrorBox(state.error, onRetry = { vm.load() })
                else -> PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = { vm.load(pull = true) }, modifier = Modifier.fillMaxSize()) {
                    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Stat("À venir", state.items.size.toString(), Modifier.weight(1f))
                                Stat("Points saison", state.seasonPoints?.toString() ?: "–", Modifier.weight(1f))
                            }
                        }
                        if (state.items.isEmpty()) {
                            item { EmptyBox(Icons.Outlined.Shield, "Aucune affectation", "Inscrivez-vous à un poste depuis le calendrier des officiels.") }
                        }
                        items(state.items, key = { it.slotId }) { a ->
                            SectionCard(title = a.matchTitle, modifier = Modifier.clickable { vm.open(a) }, trailing = { StatusBadge(a.role, Tone.Success) }) {
                                Text(a.startsAt.formatDayTime(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String, modifier: Modifier) {
    KCard(modifier) {
        KCardContent {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
