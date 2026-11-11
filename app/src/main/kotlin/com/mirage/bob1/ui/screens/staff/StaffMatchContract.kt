package com.mirage.bob1.ui.screens.staff

import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.model.MatchStatus
import com.mirage.bob1.domain.model.Slot
import java.time.Instant

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

/** What [StaffMatchContent] can ask for; keeps the content stateless and previewable. */
data class StaffMatchActions(
    val ask: (Slot) -> Unit,
    val confirm: () -> Unit,
    val dismiss: () -> Unit,
    val load: () -> Unit,
    val back: () -> Unit,
)
