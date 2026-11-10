package com.mirage.bob1.ui.screens.staff

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import javax.inject.Inject
import com.mirage.bob1.domain.model.PointEntry
import com.mirage.bob1.domain.model.Ranking
import com.mirage.bob1.domain.usecase.GetMyPointEntriesUseCase
import com.mirage.bob1.domain.usecase.GetStaffRankingUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatShort
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import com.mirage.bob1.ui.core.userMessage
import com.mirage.bob1.ui.screens.ranking.RankingRowCard

data class StaffRankingState(
    val loading: Boolean = true,
    val error: String? = null,
    val ranking: Ranking? = null,
    val entries: List<PointEntry> = emptyList(),
)

@HiltViewModel
class StaffRankingViewModel @Inject constructor(
    application: Application,
    private val getRanking: GetStaffRankingUseCase,
    private val getEntries: GetMyPointEntriesUseCase,
) : Bob1ViewModel<StaffRankingState>(StaffRankingState(), application) {

    init { load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getRanking() }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { r ->
            updateState { copy(loading = false, ranking = r) }
        }
        call({ getEntries() }, onError = {}) { e -> updateState { copy(entries = e) } }
    }
}

@Composable
fun StaffRankingScreen(navController: NavController, viewModel: StaffRankingViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Classement des officiels")
            val r = state.ranking
            when {
                state.loading && r == null -> LoadingBox()
                r == null -> ErrorBox(state.error ?: "Classement indisponible", onRetry = vm::load)
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Saison ${r.season}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    r.mine?.let { m -> item { RankingRowCard(m, highlighted = true) } }
                    if (r.rows.isEmpty()) item { EmptyBox(Icons.Outlined.EmojiEvents, "Pas encore de classement", "Les points arrivent après les premières inscriptions.") }
                    items(r.rows, key = { it.id }) { row -> RankingRowCard(row, highlighted = row.isMine) }
                    if (state.entries.isNotEmpty()) {
                        item { Spacer(Modifier.height(6.dp)) }
                        item {
                            SectionCard(title = "Historique de mes points") {
                                state.entries.forEach { e -> EntryLine(e) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryLine(e: PointEntry) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.weight(1f)) {
            Text(e.reason.ifBlank { e.category }, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Text(e.createdAt.formatShort(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        StatusBadge(if (e.points > 0) "+${e.points} pts" else "${e.points} pts", if (e.points < 0) Tone.Danger else Tone.Success)
    }
}
