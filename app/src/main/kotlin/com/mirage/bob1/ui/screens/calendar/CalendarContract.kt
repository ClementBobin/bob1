package com.mirage.bob1.ui.screens.calendar

import com.mirage.bob1.domain.model.Division
import com.mirage.bob1.domain.model.FamilyMember
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.ui.core.toLocalDate
import java.time.LocalDate
import java.time.YearMonth

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

/** What [CalendarContent] can ask for; keeps the content stateless and previewable. */
data class CalendarActions(
    val changeMonth: (Long) -> Unit,
    val filterChild: (String?) -> Unit,
    val filterDivision: (String?) -> Unit,
    val openMatch: (Match) -> Unit,
    val openNotifications: () -> Unit,
    val refresh: () -> Unit,
    val selectDate: (LocalDate) -> Unit,
)
