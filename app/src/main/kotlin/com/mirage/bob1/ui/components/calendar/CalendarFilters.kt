package com.mirage.bob1.ui.components.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.mirage.bob1.ui.components.PersonAvatar
import com.mirage.bob1.ui.screens.calendar.CalendarActions
import com.mirage.bob1.ui.screens.calendar.CalendarState
import dev.kindling.core.components.ui.avatar.KAvatarSize

@Composable
fun CalendarFilters(state: CalendarState, actions: CalendarActions) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip(selected = state.divisionFilter == null && state.childFilter == null, onClick = { actions.filterDivision(null) }, label = { Text("Tous") }) }
            items(state.divisions, key = { it.id }) { d ->
                FilterChip(selected = state.divisionFilter == d.id, onClick = { actions.filterDivision(d.id) }, label = { Text(d.name) })
            }
        }
        if (state.children.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.children, key = { it.userId }) { c ->
                    FilterChip(
                        selected = state.childFilter == c.userId,
                        onClick = { actions.filterChild(if (state.childFilter == c.userId) null else c.userId) },
                        label = { Text(c.firstName) },
                        leadingIcon = { PersonAvatar(c.initials, KAvatarSize.Sm) },
                    )
                }
            }
        }
    }
}
