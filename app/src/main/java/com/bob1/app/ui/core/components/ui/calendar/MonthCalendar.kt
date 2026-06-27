package com.bob1.app.ui.core.components.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bob1.app.domain.model.Match
import java.util.Calendar

@Composable
fun MonthCalendar(
    year: Int,
    month: Int,
    matches: List<Match>,
    selectedDivisionIds: Set<String>,
    onDayTapped: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cal = Calendar.getInstance().apply { set(year, month - 1, 1) }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    // Day of week of the 1st (0=Sun, adjust to Mon-first)
    val firstDow = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7

    val dayNames = listOf("L", "M", "M", "J", "V", "S", "D")

    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        // Day names header
        Row(Modifier.fillMaxWidth()) {
            dayNames.forEach { d ->
                Text(
                    d,
                    modifier  = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style     = MaterialTheme.typography.labelSmall,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Grid
        val cells = buildList {
            repeat(firstDow) { add(null) } // empty leading cells
            (1..daysInMonth).forEach { add(it) }
        }
        val rows = cells.chunked(7)

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            rows.forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) { col ->
                        val day = row.getOrNull(col)
                        DayCell(
                            day                 = day,
                            matches             = if (day != null) matches.filter { m ->
                                val dMonth = m.dateIso.substring(5, 7).toIntOrNull() ?: 0
                                val dDay   = m.dateIso.substring(8, 10).toIntOrNull() ?: 0
                                val dYear  = m.dateIso.substring(0, 4).toIntOrNull() ?: 0
                                dYear == year && dMonth == month && dDay == day
                            }.filter { m ->
                                selectedDivisionIds.isEmpty() || m.divisionId in selectedDivisionIds
                            } else emptyList(),
                            onTap               = { day?.let { onDayTapped(it) } },
                            modifier            = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}