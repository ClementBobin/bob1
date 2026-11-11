package com.mirage.bob1.ui.screens.match

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.ErrorBox
import com.mirage.bob1.ui.components.LoadingBox
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.components.match.CarpoolSection
import com.mirage.bob1.ui.components.match.PresenceSection
import com.mirage.bob1.ui.components.match.VolunteerSection
import com.mirage.bob1.ui.core.formatDayTime
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun MatchScreen(navController: NavController, viewModel: MatchViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(
        viewModel = viewModel, navController = navController,
        onEvent = { _, _, event -> handle(event) },
    ) { state, vm ->
        MatchContent(
            state = state,
            actions = MatchActions(
                cancelMyRide = vm::cancelMyRide,
                confirmFamilyPresence = vm::confirmFamilyPresence,
                load = vm::load,
                offerSeats = vm::offerSeats,
                openDirections = vm::openDirections,
                setSeats = vm::setSeats,
                toggleBooking = vm::toggleBooking,
                toggleSlot = vm::toggleSlot,
                back = { navController.popBackStack() },
            ),
        )
    }
}

@Composable
fun MatchContent(state: MatchState, actions: MatchActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Détail du match", onBack = { actions.back() })
        val match = state.match
        when {
            state.loading -> LoadingBox()
            match == null -> ErrorBox(state.error ?: "Match introuvable", onRetry = actions.load)
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
                            KButton("Itinéraire", actions.openDirections, variant = KButtonVariant.Secondary, size = KButtonSize.Sm)
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(listOfNotNull(v.address, v.city).joinToString(", ").ifBlank { "Adresse non renseignée" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                item { PresenceSection(state, actions) }
                item { VolunteerSection(state, actions) }
                item { CarpoolSection(state, actions) }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}
