package com.mirage.bob1.ui.screens.staff

import android.app.Application
import androidx.compose.foundation.lazy.items
import com.mirage.bob1.domain.model.StaffAssignment
import com.mirage.bob1.domain.usecase.GetAssignmentsUseCase
import com.mirage.bob1.domain.usecase.GetStaffRankingUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.StaffMatchDestination
import com.mirage.bob1.ui.core.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffAssignmentsViewModel @Inject constructor(
    application: Application,
    private val getAssignments: GetAssignmentsUseCase,
    private val getStaffRanking: GetStaffRankingUseCase,
) : Bob1ViewModel<StaffAssignmentsState>(StaffAssignmentsState(), application) {

    init { load() }

    fun load() = load(pull = false)
    fun pull() = load(pull = true)

    private fun load(pull: Boolean) {
        updateState { copy(loading = !pull && items.isEmpty(), refreshing = pull, error = null) }
        call(
            block = { getAssignments() },
            onError = { updateState { copy(loading = false, refreshing = false, error = if (items.isEmpty()) it.userMessage() else null) }; sendError(it) },
        ) { list -> updateState { copy(loading = false, refreshing = false, items = list) } }
        call({ getStaffRanking() }, onError = {}) { r -> updateState { copy(seasonPoints = r.mine?.points) } }
    }

    fun open(a: StaffAssignment) = navigate(StaffMatchDestination(a.matchId))
}
