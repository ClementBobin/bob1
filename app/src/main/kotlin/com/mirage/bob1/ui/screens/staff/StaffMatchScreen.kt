package org.bob1.app.ui.screens.staff

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant
import java.time.Instant
import javax.inject.Inject
import org.bob1.app.domain.model.Match
import org.bob1.app.domain.model.MatchStatus
import org.bob1.app.domain.model.Slot
import org.bob1.app.domain.usecase.*
import org.bob1.app.ui.core.Bob1ViewModel
import org.bob1.app.ui.core.components.*
import org.bob1.app.ui.core.formatDayTime
import org.bob1.app.ui.core.navigation.HapticConfirm
import org.bob1.app.ui.core.navigation.MatchDestination
import org.bob1.app.ui.core.navigation.rememberUiEventHandler
import org.bob1.app.ui.core.userMessage

/** Sign up or leave: both go through a confirmation dialog, like the design's "Voulez-vous vous inscrire comme Arbitre 1 ?". */
data class PendingSlotAction(val slot: Slot, val join: Boolean)

data class StaffMatchState(
    val loading: Boolean = true,
    val error: String? = null,
    val match: Match? = null,
    val slots: List<Slot> = emptyList(),
    val pending: PendingSlotAction? = null,
    val busy: Boolean = false,
) {
    val isOpen get() = match?.let { it.status == MatchStatus.Scheduled && it.startsAt.isAfter(Instant.now()) } ?: false
}

@HiltViewModel
class StaffMatchViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val getMatch: GetMatchUseCase,
    private val getParticipation: GetParticipationUseCase,
    private val joinSlot: JoinSlotUseCase,
    private val leaveSlot: LeaveSlotUseCase,
) : Bob1ViewModel<StaffMatchState>(StaffMatchState(), application) {

    private val matchId: String = checkNotNull(savedStateHandle[MatchDestination.ARG])

    init { load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getMatch(matchId) }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { m ->
            updateState { copy(match = m, loading = false) }
            reloadSlots()
        }
    }

    private fun reloadSlots() = call({ getParticipation(matchId) }, onError = ::sendError) { p ->
        updateState { copy(slots = p.slots.filter { it.kind.equals("Staff", true) }) }
    }

    fun ask(slot: Slot) = updateState { copy(pending = PendingSlotAction(slot, join = !slot.mine)) }
    fun dismiss() = updateState { copy(pending = null) }

    fun confirm() {
        val action = state.value.pending ?: return
        updateState { copy(pending = null, busy = true) }
        val block: suspend () -> Result<Unit> = { if (action.join) joinSlot(action.slot.id) else leaveSlot(action.slot.id) }
        call(block, onError = { updateState { copy(busy = false) }; sendError(it); reloadSlots() }) {
            updateState { copy(busy = false) }
            sendEvent(HapticConfirm)
            sendInfo(if (action.join) "Inscription confirmée" else "Inscription annulée")
            reloadSlots()
        }
    }
}

@Composable
fun StaffMatchScreen(navController: NavController, viewModel: StaffMatchViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onBack = { _, _ -> navController.popBackStack() }, onEvent = { _, _, e -> handle(e) },
    ) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Officiels du match", onBack = { navController.popBackStack() })
            val match = state.match
            when {
                state.loading -> LoadingBox()
                match == null -> ErrorBox(state.error ?: "Match introuvable", onRetry = vm::load)
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        SectionCard(title = match.title) {
                            Text(match.startsAt.formatDayTime(), fontWeight = FontWeight.SemiBold)
                            Text(match.divisionName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (!state.isOpen) StatusBadge("Inscriptions closes", Tone.Danger)
                        }
                    }
                    item {
                        SectionCard(title = "Postes d'officiels") {
                            if (state.slots.isEmpty()) Text("Aucun poste d'officiel pour ce match.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            state.slots.forEach { slot -> RoleSlot(slot, enabled = state.isOpen && !state.busy, onClick = { vm.ask(slot) }) }
                        }
                    }
                }
            }
        }
        state.pending?.let { p ->
            ConfirmDialog(
                title = if (p.join) "Voulez-vous vous inscrire comme ${p.slot.role} ?" else "Vous désinscrire du poste ${p.slot.role} ?",
                message = state.match?.let { "${it.title} · ${it.startsAt.formatDayTime()}." }.orEmpty(),
                confirmText = if (p.join) "Oui, je m'inscris" else "Oui, me désinscrire",
                dismissText = "Non",
                onConfirm = vm::confirm, onDismiss = vm::dismiss,
            )
        }
    }
}

@Composable
private fun RoleSlot(slot: Slot, enabled: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PersonAvatar(slot.role.take(1).uppercase())
        Column(Modifier.weight(1f)) {
            Text(slot.role, fontWeight = FontWeight.SemiBold)
            Text(
                when { slot.mine -> "Vous êtes inscrit"; slot.isFull -> "Pourvu"; else -> "Poste disponible" },
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        when {
            slot.mine -> KButton("Me désinscrire", onClick, variant = KButtonVariant.Outline, size = KButtonSize.Sm, enabled = enabled)
            slot.isFull -> StatusBadge("Complet", Tone.Neutral)
            else -> KButton("S'inscrire", onClick, variant = KButtonVariant.Secondary, size = KButtonSize.Sm, enabled = enabled)
        }
    }
}
