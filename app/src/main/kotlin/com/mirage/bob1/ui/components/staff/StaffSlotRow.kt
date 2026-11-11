package com.mirage.bob1.ui.components.staff

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Slot
import com.mirage.bob1.ui.components.PersonAvatar
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun StaffSlotRow(slot: Slot, enabled: Boolean, onClick: () -> Unit) {
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
