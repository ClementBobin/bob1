package org.bob1.app.ui.screens.ranking

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
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant
import dev.kindling.core.components.ui.card.KCard
import dev.kindling.core.components.ui.card.KCardContent
import javax.inject.Inject
import org.bob1.app.domain.model.Ranking
import org.bob1.app.domain.model.RankingRow
import org.bob1.app.domain.usecase.GetRankingUseCase
import org.bob1.app.ui.core.Bob1ViewModel
import org.bob1.app.ui.core.components.*
import org.bob1.app.ui.core.navigation.rememberUiEventHandler
import org.bob1.app.ui.core.userMessage

data class RankingState(val family: Boolean = false, val loading: Boolean = true, val error: String? = null, val ranking: Ranking? = null)

@HiltViewModel
class RankingViewModel @Inject constructor(
    application: Application,
    private val getRanking: GetRankingUseCase,
) : Bob1ViewModel<RankingState>(RankingState(), application) {

    init { load() }

    fun setFamily(family: Boolean) { updateState { copy(family = family) }; load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getRanking(state.value.family) }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { r ->
            updateState { copy(loading = false, ranking = r) }
        }
    }
}

@Composable
fun RankingScreen(navController: NavController, viewModel: RankingViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Classement")
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KButton("Personnes", { vm.setFamily(false) }, Modifier.weight(1f), variant = if (!state.family) KButtonVariant.Default else KButtonVariant.Outline)
                KButton("Familles", { vm.setFamily(true) }, Modifier.weight(1f), variant = if (state.family) KButtonVariant.Default else KButtonVariant.Outline)
            }
            val r = state.ranking
            when {
                state.loading && r == null -> LoadingBox()
                r == null -> ErrorBox(state.error ?: "Classement indisponible", onRetry = vm::load)
                r.rows.isEmpty() -> EmptyBox(Icons.Outlined.EmojiEvents, "Pas encore de classement", "Les points apparaîtront après les premiers matchs.")
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Saison ${r.season}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    r.mine?.let { mine -> item { RankingRowCard(mine, highlighted = true) } }
                    items(r.rows, key = { it.id }) { row -> RankingRowCard(row, highlighted = row.isMine) }
                }
            }
        }
    }
}

@Composable
fun RankingRowCard(row: RankingRow, highlighted: Boolean) {
    KCard(Modifier.fillMaxWidth()) {
        KCardContent {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("#${row.rank}", fontWeight = FontWeight.ExtraBold, color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(40.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (row.isMine) "${row.name} (vous)" else row.name, fontWeight = FontWeight.SemiBold)
                    row.subtitle?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                StatusBadge("${row.points} pts", if (row.points < 0) Tone.Danger else if (highlighted) Tone.Pending else Tone.Success)
            }
        }
    }
}
