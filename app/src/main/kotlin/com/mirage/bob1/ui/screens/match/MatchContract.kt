package com.mirage.bob1.ui.screens.match

import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.model.MatchStatus
import com.mirage.bob1.domain.model.Participation
import com.mirage.bob1.domain.model.Ride
import com.mirage.bob1.domain.model.Slot
import com.mirage.bob1.domain.model.Venue
import java.time.Instant

data class MatchState(
    val loading: Boolean = true,
    val error: String? = null,
    val match: Match? = null,
    val venue: Venue? = null,
    val participation: Participation? = null,
    val rides: List<Ride> = emptyList(),
    val seats: Int = 3,
    val busy: Boolean = false,
) {
    /** Sign-ups are closed for cancelled / played / already started matches (the API answers 409 `match.closed`). */
    val isOpen: Boolean get() = match?.let { it.status == MatchStatus.Scheduled && it.startsAt.isAfter(Instant.now()) } ?: false
    val volunteerSlots: List<Slot> get() = participation?.slots?.filter { it.kind.equals("Volunteer", true) }.orEmpty()
    val myRide: Ride? get() = rides.firstOrNull { it.isMine }
}

/** What [MatchContent] can ask for; keeps the content stateless and previewable. */
data class MatchActions(
    val cancelMyRide: (Ride) -> Unit,
    val confirmFamilyPresence: () -> Unit,
    val load: () -> Unit,
    val offerSeats: () -> Unit,
    val openDirections: () -> Unit,
    val setSeats: (Int) -> Unit,
    val toggleBooking: (Ride) -> Unit,
    val toggleSlot: (Slot) -> Unit,
    val back: () -> Unit,
)
