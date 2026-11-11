package com.mirage.bob1.ui.screens.ranking

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
import com.mirage.bob1.ui.components.ranking.RankingRowCard
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun RankingScreen(navController: NavController, viewModel: RankingViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        RankingContent(
            state = state,
            actions = RankingActions(
                load = vm::load,
                setFamily = vm::setFamily,
            ),
        )
    }
}

@Composable
fun RankingContent(state: RankingState, actions: RankingActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Classement")
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KButton("Personnes", { actions.setFamily(false) }, Modifier.weight(1f), variant = if (!state.family) KButtonVariant.Default else KButtonVariant.Outline)
            KButton("Familles", { actions.setFamily(true) }, Modifier.weight(1f), variant = if (state.family) KButtonVariant.Default else KButtonVariant.Outline)
        }
        val r = state.ranking
        when {
            state.loading && r == null -> LoadingBox()
            r == null -> ErrorBox(state.error ?: "Classement indisponible", onRetry = actions.load)
            r.rows.isEmpty() -> EmptyBox(Icons.Outlined.EmojiEvents, "Pas encore de classement", "Les points apparaîtront après les premiers matchs.")
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Text("Saison ${r.season}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                r.mine?.let { mine -> item { RankingRowCard(mine, highlighted = true) } }
                items(r.rows, key = { it.id }) { row -> RankingRowCard(row, highlighted = row.isMine) }
            }
        }
    }
}
