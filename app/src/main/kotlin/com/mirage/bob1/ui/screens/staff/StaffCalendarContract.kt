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
    val unread: Int = 0,
) {
    val byDay: Map<LocalDate, List<OpenStaffMatch>> get() = matches.groupBy { it.startsAt.toLocalDate() }.toSortedMap()
}

/** What [StaffCalendarContent] can ask for; keeps the content stateless and previewable. */
data class StaffCalendarActions(
    val load: () -> Unit,
    val open: (OpenStaffMatch) -> Unit,
    val openNotifications: () -> Unit,
    val pull: () -> Unit,
)
