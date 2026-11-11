package com.mirage.bob1.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.ui.components.EmptyBox
import com.mirage.bob1.ui.components.MatchRow
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.core.formatLong
import java.time.LocalDate

@Composable
fun CalendarDayPanel(date: LocalDate?, byDate: Map<LocalDate, List<Match>>, committed: Set<String>, onOpen: (Match) -> Unit) {
    if (date == null) return
    val list = byDate[date].orEmpty()
    SectionCard(title = date.formatLong(), trailing = { Text(if (list.size == 1) "1 match" else "${list.size} matchs", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }) {
        if (list.isEmpty()) {
            EmptyBox(Icons.Outlined.EventBusy, "Aucun match", "Rien de prévu ce jour-là.")
        } else {
            list.forEach { m -> MatchRow(m, committed = m.id in committed, onClick = { onOpen(m) }) }
        }
    }
}
