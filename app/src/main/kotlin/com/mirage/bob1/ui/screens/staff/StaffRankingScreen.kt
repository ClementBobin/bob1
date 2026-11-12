package com.mirage.bob1.ui.screens.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.mirage.bob1.ui.components.SeasonSelector
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.ranking.RankingRowCard
import com.mirage.bob1.ui.components.staff.PointEntryRow
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen

@Composable
fun StaffRankingScreen(navController: NavController, viewModel: StaffRankingViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        StaffRankingContent(
            state = state,
            actions = StaffRankingActions(
                load = vm::load,
                selectSeason = vm::selectSeason,
            ),
        )
    }
}

@Composable
fun StaffRankingContent(state: StaffRankingState, actions: StaffRankingActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Classement des officiels")
        SeasonSelector(state.seasons, state.season, actions.selectSeason, Modifier.padding(top = 8.dp))
        val r = state.ranking
        when {
            state.loading && r == null -> LoadingBox()
            r == null -> ErrorBox(state.error ?: "Classement indisponible", onRetry = actions.load)
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Saison ${r.season}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                r.mine?.let { m -> item { RankingRowCard(m, highlighted = true) } }
                if (r.rows.isEmpty()) item { EmptyBox(Icons.Outlined.EmojiEvents, "Pas encore de classement", "Les points arrivent après les premières inscriptions.") }
                items(r.rows, key = { it.id }) { row -> RankingRowCard(row, highlighted = row.isMine) }
                if (state.entries.isNotEmpty()) {
                    item { Spacer(Modifier.height(6.dp)) }
                    item {
                        SectionCard(title = "Historique de mes points") {
                            state.entries.forEach { e -> PointEntryRow(e) }
                        }
                    }
                }
            }
        }
    }
}
