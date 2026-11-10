package com.mirage.bob1.ui.screens.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant
import com.mirage.bob1.domain.model.Ride
import com.mirage.bob1.domain.model.Slot
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatDayTime
import com.mirage.bob1.ui.core.formatTime
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler

@Composable
fun MatchScreen(navController: NavController, viewModel: MatchViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onBack = { _, _ -> navController.popBackStack() },
        onEvent = { _, _, event -> handle(event) },
    ) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Détail du match", onBack = { navController.popBackStack() })
            val match = state.match
            when {
                state.loading -> LoadingBox()
                match == null -> ErrorBox(state.error ?: "Match introuvable", onRetry = vm::load)
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        SectionCard(title = match.title) {
                            Text(match.startsAt.formatDayTime(), fontWeight = FontWeight.SemiBold)
                            Text(match.divisionName, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (!state.isOpen) StatusBadge(if (match.status.name == "Cancelled") "Match annulé" else "Inscriptions closes", Tone.Danger)
                        }
                    }
                    item {
                        state.venue?.let { v ->
                            SectionCard(title = v.name, trailing = {
                                KButton("Itinéraire", vm::openDirections, variant = KButtonVariant.Secondary, size = KButtonSize.Sm)
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(listOfNotNull(v.address, v.city).joinToString(", ").ifBlank { "Adresse non renseignée" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    item { PresenceSection(state, vm) }
                    item { VolunteerSection(state, vm) }
                    item { CarpoolSection(state, vm) }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PresenceSection(state: MatchState, vm: MatchViewModel) {
    val confirmed = state.participation?.presenceConfirmed == true
    SectionCard(title = "Présence de la famille", step = 1, trailing = { if (confirmed) StatusBadge("Confirmée", Tone.Success) }) {
        Text("Confirmez que votre famille sera présente à ce match.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        KButton(
            text = if (confirmed) "Présence confirmée" else "Confirmer ma présence",
            onClick = vm::confirmFamilyPresence, modifier = Modifier.fillMaxWidth(),
            enabled = state.isOpen && !confirmed && !state.busy,
        )
    }
}

@Composable
private fun VolunteerSection(state: MatchState, vm: MatchViewModel) {
    SectionCard(title = "Buvette & bénévolat", step = 2) {
        if (state.volunteerSlots.isEmpty()) {
            Text("Aucun poste à pourvoir pour ce match.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else state.volunteerSlots.forEach { slot -> SlotRow(slot, enabled = state.isOpen && !state.busy, onToggle = { vm.toggleSlot(slot) }) }
    }
}

@Composable
private fun SlotRow(slot: Slot, enabled: Boolean, onToggle: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(slot.role, fontWeight = FontWeight.SemiBold)
                val hours = if (slot.startsAt != null && slot.endsAt != null) "De ${slot.startsAt.formatTime()} à ${slot.endsAt.formatTime()} · " else ""
                Text("$hours${slot.filled}/${slot.capacity} pourvus", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(if (slot.isFull) "Complet" else "${slot.missing} place${if (slot.missing > 1) "s" else ""}", if (slot.isFull) Tone.Neutral else Tone.Success)
        }
        LinearProgressIndicator(
            progress = { if (slot.capacity == 0) 0f else slot.filled / slot.capacity.toFloat() },
            modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary,
        )
        KButton(
            text = if (slot.mine) "Me désinscrire" else "Je m'inscris",
            onClick = onToggle, modifier = Modifier.fillMaxWidth(),
            variant = if (slot.mine) KButtonVariant.Outline else KButtonVariant.Default,
            enabled = enabled && (slot.mine || !slot.isFull),
        )
    }
}

@Composable
private fun CarpoolSection(state: MatchState, vm: MatchViewModel) {
    SectionCard(title = "Covoiturage", step = 3) {
        if (state.myRide == null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.DirectionsCar, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(Modifier.weight(1f)) {
                    Text("Places disponibles", fontWeight = FontWeight.SemiBold)
                    Text("Hors conducteur", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton({ vm.setSeats(state.seats - 1) }) { Icon(Icons.Outlined.Remove, "Moins") }
                Text(state.seats.toString(), fontWeight = FontWeight.ExtraBold)
                IconButton({ vm.setSeats(state.seats + 1) }) { Icon(Icons.Outlined.Add, "Plus") }
            }
            KButton("Proposer ${state.seats} places", vm::offerSeats, Modifier.fillMaxWidth(), enabled = state.isOpen && !state.busy)
        }
        if (state.rides.isEmpty()) {
            Text("Aucun conducteur pour le moment.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        } else state.rides.forEach { ride -> RideRow(ride, enabled = state.isOpen && !state.busy, onBook = { vm.toggleBooking(ride) }, onCancel = { vm.cancelMyRide(ride) }) }
    }
}

@Composable
private fun RideRow(ride: Ride, enabled: Boolean, onBook: () -> Unit, onCancel: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PersonAvatar(ride.driverName.split(" ").take(2).joinToString("") { it.take(1) })
        Column(Modifier.weight(1f)) {
            Text(if (ride.isMine) "${ride.driverName} (vous)" else ride.driverName, fontWeight = FontWeight.SemiBold)
            Text("Départ ${ride.departure.formatTime()} · ${ride.seatsLeft}/${ride.seats} places", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            ride.notes?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        when {
            ride.isMine -> KButton("Annuler", onCancel, variant = KButtonVariant.Outline, size = KButtonSize.Sm, enabled = enabled)
            ride.bookedByMe -> KButton("Annuler", onBook, variant = KButtonVariant.Outline, size = KButtonSize.Sm, enabled = enabled)
            else -> KButton("Réserver", onBook, size = KButtonSize.Sm, enabled = enabled && ride.seatsLeft > 0)
        }
    }
}
