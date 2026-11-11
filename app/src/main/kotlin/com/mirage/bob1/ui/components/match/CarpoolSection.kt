package com.mirage.bob1.ui.components.match

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.screens.match.MatchActions
import com.mirage.bob1.ui.screens.match.MatchState
import dev.kindling.core.components.ui.button.KButton

@Composable
fun CarpoolSection(state: MatchState, actions: MatchActions) {
    SectionCard(title = "Covoiturage", step = 3) {
        if (state.myRide == null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.DirectionsCar, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(Modifier.weight(1f)) {
                    Text("Places disponibles", fontWeight = FontWeight.SemiBold)
                    Text("Hors conducteur", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton({ actions.setSeats(state.seats - 1) }) { Icon(Icons.Outlined.Remove, "Moins") }
                Text(state.seats.toString(), fontWeight = FontWeight.ExtraBold)
                IconButton({ actions.setSeats(state.seats + 1) }) { Icon(Icons.Outlined.Add, "Plus") }
            }
            KButton("Proposer ${state.seats} places", actions.offerSeats, Modifier.fillMaxWidth(), enabled = state.isOpen && !state.busy)
        }
        if (state.rides.isEmpty()) {
            Text("Aucun conducteur pour le moment.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        } else state.rides.forEach { ride -> RideRow(ride, enabled = state.isOpen && !state.busy, onBook = { actions.toggleBooking(ride) }, onCancel = { actions.cancelMyRide(ride) }) }
    }
}
