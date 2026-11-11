package com.mirage.bob1.ui.screens.staff

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.mirage.bob1.domain.model.Slot
import com.mirage.bob1.domain.usecase.GetMatchUseCase
import com.mirage.bob1.domain.usecase.GetParticipationUseCase
import com.mirage.bob1.domain.usecase.JoinSlotUseCase
import com.mirage.bob1.domain.usecase.LeaveSlotUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.HapticConfirm
import com.mirage.bob1.ui.core.navigation.MatchDestination
import com.mirage.bob1.ui.core.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffMatchViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val getMatch: GetMatchUseCase,
    private val getParticipation: GetParticipationUseCase,
    private val joinSlot: JoinSlotUseCase,
    private val leaveSlot: LeaveSlotUseCase,
) : Bob1ViewModel<StaffMatchState>(StaffMatchState(), application) {

    private val matchId: String = checkNotNull(savedStateHandle[MatchDestination.ARG])

    init { load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getMatch(matchId) }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { m ->
            updateState { copy(match = m, loading = false) }
            reloadSlots()
        }
    }

    private fun reloadSlots() = call({ getParticipation(matchId) }, onError = ::sendError) { p ->
        updateState { copy(slots = p.slots.filter { it.kind.equals("Staff", true) }) }
    }

    fun ask(slot: Slot) = updateState { copy(pending = PendingSlotAction(slot, join = !slot.mine)) }
    fun dismiss() = updateState { copy(pending = null) }

    fun confirm() {
        val action = state.value.pending ?: return
        updateState { copy(pending = null, busy = true) }
        val block: suspend () -> Result<Unit> = { if (action.join) joinSlot(action.slot.id) else leaveSlot(action.slot.id) }
        call(block, onError = { updateState { copy(busy = false) }; sendError(it); reloadSlots() }) {
            updateState { copy(busy = false) }
            sendEvent(HapticConfirm)
            sendInfo(if (action.join) "Inscription confirmée" else "Inscription annulée")
            reloadSlots()
        }
    }
}
