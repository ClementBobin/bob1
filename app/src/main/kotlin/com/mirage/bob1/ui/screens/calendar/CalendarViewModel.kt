package com.mirage.bob1.ui.screens.calendar

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import com.mirage.bob1.domain.model.Division
import com.mirage.bob1.domain.model.FamilyMember
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.usecase.*
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.MatchDestination
import com.mirage.bob1.ui.core.navigation.NotificationsDestination
import com.mirage.bob1.ui.core.toLocalDate

data class CalendarState(
    val firstName: String = "",
    val month: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = LocalDate.now(),
    val matches: List<Match> = emptyList(),
    val committedMatchIds: Set<String> = emptySet(),
    val divisions: List<Division> = emptyList(),
    val children: List<FamilyMember> = emptyList(),
    val divisionFilter: String? = null,   // division id
    val childFilter: String? = null,      // family member user id
    val unread: Int = 0,
    val refreshing: Boolean = false,
) {
    /** A child filter wins over a division chip (it maps to that child's division). */
    private val effectiveDivisionId: String?
        get() = children.firstOrNull { it.userId == childFilter }?.divisionId ?: divisionFilter

    val visibleMatches: List<Match>
        get() = effectiveDivisionId?.let { id -> matches.filter { it.divisionId == id } } ?: matches

    val matchesByDate: Map<LocalDate, List<Match>>
        get() = visibleMatches.groupBy { it.startsAt.toLocalDate() }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    application: Application,
    private val observeMatches: ObserveMatchesUseCase,
    private val refreshMatches: RefreshMatchesUseCase,
    private val observeCommitments: ObserveCommitmentsUseCase,
    private val refreshCommitments: RefreshCommitmentsUseCase,
    private val observeUnread: ObserveUnreadCountUseCase,
    private val refreshNotifications: RefreshNotificationsUseCase,
    private val getMe: GetMeUseCase,
    private val getFamily: GetFamilyUseCase,
    private val getDivisions: GetDivisionsUseCase,
) : Bob1ViewModel<CalendarState>(CalendarState(), application) {

    private val zone = ZoneId.systemDefault()
    private val monthFlow = MutableStateFlow(YearMonth.now())

    init {
        // Offline-first: the calendar always renders what Room has; refresh() only updates Room.
        viewModelScope.launch {
            monthFlow.flatMapLatest { m -> observeMatches(m.start(), m.end()) }
                .collect { list -> updateState { copy(matches = list) } }
        }
        viewModelScope.launch { observeCommitments().collect { c -> updateState { copy(committedMatchIds = c.map { it.matchId }.toSet()) } } }
        viewModelScope.launch { observeUnread().collect { n -> updateState { copy(unread = n) } } }

        call({ getMe() }, onError = {}) { me -> updateState { copy(firstName = me.firstName) } }
        call({ getFamily() }, onError = {}) { f -> updateState { copy(children = f.members.filter { it.isChild }) } }
        call({ getDivisions() }, onError = {}) { d -> updateState { copy(divisions = d) } }
        refresh()
    }

    private fun YearMonth.start(): Instant = atDay(1).atStartOfDay(zone).toInstant()
    private fun YearMonth.end(): Instant = atEndOfMonth().atTime(LocalTime.MAX).atZone(zone).toInstant()

    fun refresh() {
        val m = state.value.month
        updateState { copy(refreshing = true) }
        call(
            block = { refreshMatches(m.start(), m.end()) },
            onError = { updateState { copy(refreshing = false) }; sendError(it) },
            onOk = { updateState { copy(refreshing = false) } },
        )
        call({ refreshCommitments() }, onError = {})
        call({ refreshNotifications() }, onError = {})
    }

    fun changeMonth(delta: Long) {
        val next = state.value.month.plusMonths(delta)
        monthFlow.value = next
        updateState { copy(month = next, selectedDate = null) }
        refresh()
    }

    fun selectDate(date: LocalDate) = updateState { copy(selectedDate = if (selectedDate == date) null else date) }
    fun filterDivision(id: String?) = updateState { copy(divisionFilter = id, childFilter = null) }
    fun filterChild(userId: String?) = updateState { copy(childFilter = userId, divisionFilter = null) }

    fun openMatch(match: Match) = navigate(MatchDestination(match.id))
    fun openNotifications() = navigate(NotificationsDestination)
}
