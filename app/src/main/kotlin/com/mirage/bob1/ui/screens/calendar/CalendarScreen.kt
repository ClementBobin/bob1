package com.mirage.bob1.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.avatar.KAvatarSize
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonSize
import dev.kindling.core.components.ui.button.KButtonVariant
import dev.kindling.core.components.ui.card.KCard
import dev.kindling.core.components.ui.card.KCardContent
import java.time.LocalDate
import java.time.YearMonth
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatLong
import com.mirage.bob1.ui.core.formatTitle
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler

@Composable
fun CalendarScreen(navController: NavController, viewModel: CalendarViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, event -> handle(event) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(actions = { NotificationBell(state.unread, vm::openNotifications) })
            PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = vm::refresh, modifier = Modifier.fillMaxSize()) {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    item {
                        Column {
                            if (state.firstName.isNotBlank()) Text("Bonjour ${state.firstName},", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Le calendrier du club", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    item { MonthHeader(state.month, onPrev = { vm.changeMonth(-1) }, onNext = { vm.changeMonth(1) }) }
                    item { Filters(state, vm) }
                    item {
                        MonthGrid(
                            month = state.month, matchesByDate = state.matchesByDate, committed = state.committedMatchIds,
                            selected = state.selectedDate, onSelect = vm::selectDate,
                        )
                    }
                    item { Legend() }
                    item { DayPanel(state.selectedDate, state.matchesByDate, state.committedMatchIds, vm::openMatch) }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(month: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onPrev) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Mois précédent") }
        Text(month.formatTitle(), Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        IconButton(onNext) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Mois suivant") }
    }
}

@Composable
private fun Filters(state: CalendarState, vm: CalendarViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip(selected = state.divisionFilter == null && state.childFilter == null, onClick = { vm.filterDivision(null) }, label = { Text("Tous") }) }
            items(state.divisions, key = { it.id }) { d ->
                FilterChip(selected = state.divisionFilter == d.id, onClick = { vm.filterDivision(d.id) }, label = { Text(d.name) })
            }
        }
        if (state.children.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.children, key = { it.userId }) { c ->
                    FilterChip(
                        selected = state.childFilter == c.userId,
                        onClick = { vm.filterChild(if (state.childFilter == c.userId) null else c.userId) },
                        label = { Text(c.firstName) },
                        leadingIcon = { PersonAvatar(c.initials, KAvatarSize.Sm) },
                    )
                }
            }
        }
    }
}

private val weekdays = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")

@Composable
private fun MonthGrid(
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
                        DayCell(
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

@Composable
private fun DayCell(
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

@Composable
private fun Legend() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)) {
        listOf("À confirmer" to Tone.Pending, "Inscrit" to Tone.Joined, "Annulé" to Tone.Danger, "Joué" to Tone.Neutral).forEach { (label, tone) ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Box(Modifier.size(7.dp).background(toneColor(tone), CircleShape))
                Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DayPanel(date: LocalDate?, byDate: Map<LocalDate, List<Match>>, committed: Set<String>, onOpen: (Match) -> Unit) {
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
