package com.mirage.bob1.ui.screens.calendar

import com.mirage.bob1.domain.model.FamilyMember
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.model.Team
import com.mirage.bob1.ui.core.toLocalDate
import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val firstName: String = "",
    val month: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = LocalDate.now(),
    val matches: List<Match> = emptyList(),
    val committedMatchIds: Set<String> = emptySet(),
    val teams: List<Team> = emptyList(),
    val children: List<FamilyMember> = emptyList(),
    val teamFilter: String? = null,       // team id
    val childFilter: String? = null,      // family member user id
    val unread: Int = 0,
    val refreshing: Boolean = false,
) {
    /** A child filter wins over a team chip: it means "the matches of the teams this child plays in". */
    private val effectiveTeamIds: Set<String>?
        get() = children.firstOrNull { it.userId == childFilter }?.teamIds?.toSet() ?: teamFilter?.let { setOf(it) }

    val visibleMatches: List<Match>
        get() = effectiveTeamIds?.let { ids -> matches.filter { it.homeTeamId in ids || it.awayTeamId in ids } } ?: matches

    /** Only the teams that play in the displayed month, so the chips stay short. */
    val teamsInView: List<Team>
        get() {
            val ids = matches.flatMap { listOf(it.homeTeamId, it.awayTeamId) }.toSet()
            return teams.filter { it.id in ids }.sortedBy { it.name }
        }

    val matchesByDate: Map<LocalDate, List<Match>>
        get() = visibleMatches.groupBy { it.startsAt.toLocalDate() }
}

/** What [CalendarContent] can ask for; keeps the content stateless and previewable. */
data class CalendarActions(
    val changeMonth: (Long) -> Unit,
    val filterChild: (String?) -> Unit,
    val filterTeam: (String?) -> Unit,
    val openMatch: (Match) -> Unit,
    val openNotifications: () -> Unit,
    val refresh: () -> Unit,
    val selectDate: (LocalDate) -> Unit,
)
