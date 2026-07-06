package com.bob1.app.ui.screens.penalty

import android.app.Application
import com.bob1.app.domain.model.Penalty
import com.bob1.app.domain.repository.PenaltyRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object PenaltyAckContracts {
    data class UiState(
        val pending: List<Penalty> = emptyList(),
        val currentIndex: Int = 0,
    ) {
        val current: Penalty? get() = pending.getOrNull(currentIndex)
        val hasMore: Boolean  get() = currentIndex < pending.size - 1
    }

    sealed interface UiEvent {
        object AllAcknowledged : UiEvent
    }
}

class PenaltyAckViewModel(application: Application) :
    KViewModel<PenaltyAckContracts.UiState>(PenaltyAckContracts.UiState(), application) {

    private val repo: PenaltyRepository by inject()

    init { loadPending() }

    private fun loadPending() = fetchData(
        source   = { repo.getMyPenalties().getOrThrow() },
        onResult = {
            onSuccess { penalties ->
                val unacked = penalties.filter { it.acknowledgedAt == null }
                if (unacked.isEmpty()) sendEvent(PenaltyAckContracts.UiEvent.AllAcknowledged)
                else updateState { copy(pending = unacked) }
            }
            onFailure { sendEvent(PenaltyAckContracts.UiEvent.AllAcknowledged) }
        }
    )

    fun acknowledge() {
        val s = state.value
        val penalty = s.current ?: return
        fetchData(
            source   = { repo.acknowledgePenalty(penalty.id) },
            onResult = {
                onSuccess {
                    if (s.hasMore) updateState { copy(currentIndex = currentIndex + 1) }
                    else sendEvent(PenaltyAckContracts.UiEvent.AllAcknowledged)
                }
            }
        )
    }
}