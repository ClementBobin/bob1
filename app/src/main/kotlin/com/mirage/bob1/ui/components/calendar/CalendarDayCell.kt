package com.mirage.bob1.ui.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.ui.components.tone
import com.mirage.bob1.ui.components.toneColor
import java.time.LocalDate

@Composable
fun CalendarDayCell(
    date: LocalDate, inMonth: Boolean, isToday: Boolean, isSelected: Boolean,
    matches: List<Match>, committed: Set<String>, onClick: () -> Unit, modifier: Modifier,
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier.padding(2.dp).height(48.dp).clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) cs.secondaryContainer else Color.Transparent)
            .clickable(enabled = inMonth, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Text(
            date.dayOfMonth.toString(), fontSize = 14.sp,
            fontWeight = if (isToday || isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = when { !inMonth -> cs.onSurfaceVariant.copy(alpha = .4f); isToday -> cs.primary; else -> cs.onSurface },
        )
        Row(Modifier.height(8.dp).padding(top = 3.dp), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            if (inMonth) matches.take(3).forEach { m ->
                Box(Modifier.size(5.dp).background(toneColor(m.tone(m.id in committed)), CircleShape))
            }
        }
    }
}
