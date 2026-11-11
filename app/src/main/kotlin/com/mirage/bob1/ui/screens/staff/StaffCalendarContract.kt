package com.mirage.bob1.ui.screens.staff

import com.mirage.bob1.domain.model.OpenStaffMatch
import com.mirage.bob1.ui.core.toLocalDate
import java.time.LocalDate

data class StaffCalendarState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val matches: List<OpenStaffMatch> = emptyList(),
    val upcomingAssignments: Int = 0,
    val division: String? = null,
    val unread: Int = 0,
) {
    val divisions get() = matches.map { it.divisionName }.filter { it.isNotBlank() }.distinct().sorted()
    val visible get() = division?.let { d -> matches.filter { it.divisionName == d } } ?: matches
    val byDay: Map<LocalDate, List<OpenStaffMatch>> get() = visible.groupBy { it.startsAt.toLocalDate() }.toSortedMap()
}

/** What [StaffCalendarContent] can ask for; keeps the content stateless and previewable. */
data class StaffCalendarActions(
    val filter: (String?) -> Unit,
    val load: () -> Unit,
    val open: (OpenStaffMatch) -> Unit,
    val openNotifications: () -> Unit,
    val pull: () -> Unit,
)
