package com.mirage.bob1.ui.screens.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.ConfirmDialog
import com.mirage.bob1.ui.components.ErrorBox
import com.mirage.bob1.ui.components.LoadingBox
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.components.staff.StaffSlotRow
import com.mirage.bob1.ui.core.formatDayTime
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen

@Composable
fun StaffMatchScreen(navController: NavController, viewModel: StaffMatchViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onEvent = { _, _, e -> handle(e) },
    ) { state, vm ->
        StaffMatchContent(
            state = state,
            actions = StaffMatchActions(
                ask = vm::ask,
                confirm = vm::confirm,
                dismiss = vm::dismiss,
                load = vm::load,
                back = { navController.popBackStack() },
            ),
        )
    }
}

@Composable
fun StaffMatchContent(state: StaffMatchState, actions: StaffMatchActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Officiels du match", onBack = { actions.back() })
        val match = state.match
        when {
            state.loading -> LoadingBox()
            match == null -> ErrorBox(state.error ?: "Match introuvable", onRetry = actions.load)
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
                        state.slots.forEach { slot -> StaffSlotRow(slot, enabled = state.isOpen && !state.busy, onClick = { actions.ask(slot) }) }
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
            onConfirm = actions.confirm, onDismiss = actions.dismiss,
        )
    }
}
