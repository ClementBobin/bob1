package com.mirage.bob1.ui.components.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.screens.match.MatchActions
import com.mirage.bob1.ui.screens.match.MatchState

@Composable
fun VolunteerSection(state: MatchState, actions: MatchActions) {
    SectionCard(title = "Buvette & bénévolat", step = 2) {
        if (state.volunteerSlots.isEmpty()) {
            Text("Aucun poste à pourvoir pour ce match.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else state.volunteerSlots.forEach { slot -> VolunteerSlotRow(slot, enabled = state.isOpen && !state.busy, onToggle = { actions.toggleSlot(slot) }) }
    }
}
