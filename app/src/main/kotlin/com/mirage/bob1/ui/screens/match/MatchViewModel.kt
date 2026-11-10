package com.mirage.bob1.ui.screens.match

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import com.mirage.bob1.domain.model.*
import com.mirage.bob1.domain.usecase.*
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.HapticConfirm
import com.mirage.bob1.ui.core.navigation.MatchDestination
import com.mirage.bob1.ui.core.navigation.OpenDirections
import com.mirage.bob1.ui.core.userMessage

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

@HiltViewModel
class MatchViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val getMatch: GetMatchUseCase,
    private val getVenue: GetVenueUseCase,
    private val getParticipation: GetParticipationUseCase,
    private val confirmPresence: ConfirmPresenceUseCase,
    private val joinSlot: JoinSlotUseCase,
    private val leaveSlot: LeaveSlotUseCase,
    private val getRides: GetRidesUseCase,
    private val offerRide: OfferRideUseCase,
    private val cancelRide: CancelRideUseCase,
    private val bookRide: BookRideUseCase,
    private val cancelBooking: CancelBookingUseCase,
) : Bob1ViewModel<MatchState>(MatchState(), application) {

    private val matchId: String = checkNotNull(savedStateHandle[MatchDestination.ARG])

    init { load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call(
            block = { getMatch(matchId) },
            onError = { updateState { copy(loading = false, error = it.userMessage()) } },
        ) { match ->
            updateState { copy(match = match, loading = false) }
            call({ getVenue(match.venueId) }, onError = {}) { v -> updateState { copy(venue = v) } }
            reloadParticipation()
            reloadRides()
        }
    }

    private fun reloadParticipation() = call({ getParticipation(matchId) }, onError = {}) { p -> updateState { copy(participation = p) } }
    private fun reloadRides() = call({ getRides(matchId) }, onError = {}) { r -> updateState { copy(rides = r) } }

    /** Runs a write action: locks the buttons, refreshes what the action changed, confirms with a haptic + toast. */
    private fun <T> act(success: String, block: suspend () -> Result<T>, after: () -> Unit) {
        if (state.value.busy) return
        updateState { copy(busy = true) }
        call(block, onError = { updateState { copy(busy = false) }; sendError(it); after() }) {
            updateState { copy(busy = false) }
            sendEvent(HapticConfirm)
            sendInfo(success)
            after()
        }
    }

    fun confirmFamilyPresence() = act("Présence confirmée", { confirmPresence(matchId) }, ::reloadParticipation)

    fun toggleSlot(slot: Slot) =
        if (slot.mine) act("Inscription annulée", { leaveSlot(slot.id) }, ::reloadParticipation)
        else act("Inscription confirmée", { joinSlot(slot.id) }, ::reloadParticipation)

    fun setSeats(n: Int) = updateState { copy(seats = n.coerceIn(1, 8)) }

    fun offerSeats() {
        val match = state.value.match ?: return
        // Default meeting time: 1h30 before tip-off (never in the past).
        val departure = match.startsAt.minus(90, ChronoUnit.MINUTES).let { if (it.isAfter(Instant.now())) it else match.startsAt }
        act("Covoiturage proposé", { offerRide(matchId, state.value.seats, departure, null) }, ::reloadRides)
    }

    fun toggleBooking(ride: Ride) =
        if (ride.bookedByMe) act("Réservation annulée", { cancelBooking(ride.id) }, ::reloadRides)
        else act("Place réservée", { bookRide(ride.id) }, ::reloadRides)

    fun cancelMyRide(ride: Ride) = act("Covoiturage annulé", { cancelRide(ride.id) }, ::reloadRides)

    fun openDirections() {
        state.value.venue?.let { sendEvent(OpenDirections(it.fullAddress)) }
    }
}
