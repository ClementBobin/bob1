package com.mirage.bob1.ui.components.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Slot
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.core.formatTime
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun VolunteerSlotRow(slot: Slot, enabled: Boolean, onToggle: () -> Unit) {
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
