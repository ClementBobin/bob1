package com.mirage.bob1.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Match
import dev.kindling.core.components.ui.card.KCard
import dev.kindling.core.components.ui.card.KCardContent
import java.time.LocalDate
import java.time.YearMonth

private val weekdays = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")

@Composable
fun CalendarMonthGrid(
    month: YearMonth,
    matchesByDate: Map<LocalDate, List<Match>>,
    committed: Set<String>,
    selected: LocalDate?,
    onSelect: (LocalDate) -> Unit,
) {
    val first = month.atDay(1)
    val offset = first.dayOfWeek.value - 1                       // Monday first
    val rows = (offset + month.lengthOfMonth() + 6) / 7
    val gridStart = first.minusDays(offset.toLong())
    val today = LocalDate.now()

    KCard(Modifier.fillMaxWidth()) {
        KCardContent(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth()) {
                weekdays.forEach { Text(it, Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            repeat(rows) { r ->
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) { c ->
                        val date = gridStart.plusDays((r * 7 + c).toLong())
                        CalendarDayCell(
                            date = date, inMonth = YearMonth.from(date) == month, isToday = date == today, isSelected = date == selected,
                            matches = matchesByDate[date].orEmpty(), committed = committed, onClick = { onSelect(date) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
