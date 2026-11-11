package com.mirage.bob1.ui.screens.staff

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.model.OpenStaffMatch
import com.mirage.bob1.domain.usecase.GetAssignmentsUseCase
import com.mirage.bob1.domain.usecase.GetOpenStaffMatchesUseCase
import com.mirage.bob1.domain.usecase.ObserveUnreadCountUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.NotificationsDestination
import com.mirage.bob1.ui.core.navigation.StaffMatchDestination
import com.mirage.bob1.ui.core.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class StaffCalendarViewModel @Inject constructor(
    application: Application,
    private val getOpen: GetOpenStaffMatchesUseCase,
    private val getAssignments: GetAssignmentsUseCase,
    observeUnread: ObserveUnreadCountUseCase,
) : Bob1ViewModel<StaffCalendarState>(StaffCalendarState(), application) {

    init {
        viewModelScope.launch { observeUnread().collect { n -> updateState { copy(unread = n) } } }
        load()
    }

    fun load() = load(pull = false)
    fun pull() = load(pull = true)

    private fun load(pull: Boolean) {
        updateState { copy(loading = !pull && matches.isEmpty(), refreshing = pull, error = null) }
        call(
            block = { getOpen() },
            onError = { updateState { copy(loading = false, refreshing = false, error = if (matches.isEmpty()) it.userMessage() else null) }; sendError(it) },
        ) { list -> updateState { copy(loading = false, refreshing = false, matches = list) } }
        call({ getAssignments() }, onError = {}) { a -> updateState { copy(upcomingAssignments = a.size) } }
    }

    fun filter(division: String?) = updateState { copy(division = division) }
    fun open(m: OpenStaffMatch) = navigate(StaffMatchDestination(m.matchId))
    fun openNotifications() = navigate(NotificationsDestination)
}
