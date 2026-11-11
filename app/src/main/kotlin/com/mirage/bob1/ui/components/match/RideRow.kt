package com.mirage.bob1.ui.components.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Ride
import com.mirage.bob1.ui.components.PersonAvatar
import com.mirage.bob1.ui.core.formatTime
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun RideRow(ride: Ride, enabled: Boolean, onBook: () -> Unit, onCancel: () -> Unit) {
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
