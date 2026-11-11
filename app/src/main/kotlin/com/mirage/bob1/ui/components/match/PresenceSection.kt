package com.mirage.bob1.ui.components.match

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.screens.match.MatchActions
import com.mirage.bob1.ui.screens.match.MatchState
import dev.kindling.core.components.ui.button.KButton

@Composable
fun PresenceSection(state: MatchState, actions: MatchActions) {
    val confirmed = state.participation?.presenceConfirmed == true
    SectionCard(title = "Présence de la famille", step = 1, trailing = { if (confirmed) StatusBadge("Confirmée", Tone.Success) }) {
        Text("Confirmez que votre famille sera présente à ce match.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        KButton(
            text = if (confirmed) "Présence confirmée" else "Confirmer ma présence",
            onClick = actions.confirmFamilyPresence, modifier = Modifier.fillMaxWidth(),
            enabled = state.isOpen && !confirmed && !state.busy,
        )
    }
}
