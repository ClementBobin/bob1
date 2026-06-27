package com.bob1.app.ui.screens.calendar

import android.app.Application
import com.bob1.app.data.dto.OfficialRole
import com.bob1.app.domain.model.*
import com.bob1.app.domain.repository.DivisionRepository
import com.bob1.app.domain.repository.MatchRepository
import com.bob1.app.domain.repository.NotificationRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject
import java.util.Calendar

object CalendarContracts {
    data class UiState(
        val isLoading: Boolean = false,
        val year: Int  = Calendar.getInstance().get(Calendar.YEAR),
        val month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
        val divisions: List<Division> = emptyList(),
        val selectedDivisionIds: Set<String> = emptySet(),
        val matches: List<Match> = emptyList(),
        val unreadCount: Int = 0,
        val error: String? = null,
        val selectedDayMatches: List<Match> = emptyList(),
        val sheetMatch: Match? = null,
        val confirmDialogRole: OfficialRole? = null,
        val confirmDialogAction: ConfirmAction? = null,
    )

    sealed interface UiEvent {
        data class ShowError(val message: String) : UiEvent
        object NavigateToNotifications : UiEvent
    }
}

enum class ConfirmAction { SUBSCRIBE, UNSUBSCRIBE }

class CalendarViewModel(
    application: Application,
) : KViewModel<CalendarContracts.UiState>(CalendarContracts.UiState(), application) {

    private val matchRepo: MatchRepository        by inject()
    private val divisionRepo: DivisionRepository  by inject()
    private val notifRepo: NotificationRepository by inject()

    init {
        loadDivisions()
        loadMatches()
        loadUnreadCount()
    }

    private fun loadDivisions() = fetchData(
        source   = { divisionRepo.getDivisions().getOrThrow() },
        onResult = {
            onSuccess { divisions -> updateState { copy(divisions = divisions) } }
            onFailure { e -> sendEvent(CalendarContracts.UiEvent.ShowError(e.message ?: "Erreur")) }
        }
    )

    fun loadMatches() {
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source = {
                matchRepo.getMatches(
                    divisionId = null,
                    year       = state.value.year,
                    month      = state.value.month,
                ).getOrThrow()
            },
            onResult = {
                onSuccess { matches -> updateState { copy(isLoading = false, matches = matches) } }
                onFailure { e -> updateState { copy(isLoading = false, error = e.message) } }
            }
        )
    }

    private fun loadUnreadCount() = fetchData(
        source   = { notifRepo.getUnreadCount().getOrThrow() },
        onResult = {
            onSuccess { count -> updateState { copy(unreadCount = count) } }
        }
    )

    fun previousMonth() {
        val s = state.value
        val (y, m) = if (s.month == 1) s.year - 1 to 12 else s.year to s.month - 1
        updateState { copy(year = y, month = m) }
        loadMatches()
    }

    fun nextMonth() {
        val s = state.value
        val (y, m) = if (s.month == 12) s.year + 1 to 1 else s.year to s.month + 1
        updateState { copy(year = y, month = m) }
        loadMatches()
    }

    fun toggleDivision(divisionId: String) {
        val current = state.value.selectedDivisionIds
        val next = if (divisionId in current) current - divisionId else current + divisionId
        updateState { copy(selectedDivisionIds = next) }
    }

    fun selectAllDivisions() = updateState { copy(selectedDivisionIds = emptySet()) }

    fun onDayTapped(day: Int) {
        val s = state.value
        val dayMatches = s.matches.filter { match ->
            val date   = match.dateIso
            val dYear  = date.take(4).toIntOrNull()         ?: 0
            val dMonth = date.drop(5).take(2).toIntOrNull() ?: 0
            val dDay   = date.drop(8).take(2).toIntOrNull() ?: 0
            dYear == s.year && dMonth == s.month && dDay == day
        }.filter { match ->
            s.selectedDivisionIds.isEmpty() || match.divisionId in s.selectedDivisionIds
        }
        if (dayMatches.isNotEmpty()) updateState { copy(selectedDayMatches = dayMatches) }
    }

    fun selectMatchFromSheet(match: Match) = updateState {
        copy(sheetMatch = match, selectedDayMatches = emptyList())
    }

    fun dismissSheet() = updateState {
        copy(selectedDayMatches = emptyList(), sheetMatch = null,
            confirmDialogRole = null, confirmDialogAction = null)
    }

    fun onRoleTapped(match: Match, role: OfficialRole) {
        val action = if (match.currentUserRole == role) ConfirmAction.UNSUBSCRIBE else ConfirmAction.SUBSCRIBE
        updateState { copy(confirmDialogRole = role, confirmDialogAction = action) }
    }

    fun dismissConfirmDialog() = updateState {
        copy(confirmDialogRole = null, confirmDialogAction = null)
    }

    fun confirmRoleAction() {
        val s      = state.value
        val match  = s.sheetMatch          ?: return
        val role   = s.confirmDialogRole   ?: return
        val action = s.confirmDialogAction ?: return
        dismissConfirmDialog()
        when (action) {
            ConfirmAction.SUBSCRIBE   -> subscribeToMatch(match, role)
            ConfirmAction.UNSUBSCRIBE -> unsubscribeFromMatch(match)
        }
    }

    private fun subscribeToMatch(match: Match, role: OfficialRole) = fetchData(
        source   = { matchRepo.subscribeToMatch(match.id, role).getOrThrow() },
        onResult = {
            onSuccess { updated -> refreshMatchInState(updated); updateState { copy(sheetMatch = updated) } }
            onFailure { e -> sendEvent(CalendarContracts.UiEvent.ShowError(e.message ?: "Erreur")) }
        }
    )

    private fun unsubscribeFromMatch(match: Match) = fetchData(
        source   = { matchRepo.unsubscribeFromMatch(match.id).getOrThrow() },
        onResult = {
            onSuccess { updated -> refreshMatchInState(updated); updateState { copy(sheetMatch = updated) } }
            onFailure { e -> sendEvent(CalendarContracts.UiEvent.ShowError(e.message ?: "Erreur")) }
        }
    )

    private fun refreshMatchInState(updated: Match) {
        updateState { copy(matches = matches.map { if (it.id == updated.id) updated else it }) }
    }

    fun onNotificationsTapped() = sendEvent(CalendarContracts.UiEvent.NavigateToNotifications)
}