package com.bob1.app.ui.core.components.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.bob1.app.domain.model.Division

@Composable
fun DivisionFilterBar(
    divisions: List<Division>,
    selectedDivisionIds: Set<String>,
    onAllSelected: () -> Unit,
    onDivisionToggled: (String) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedDivisionIds.isEmpty(),
                onClick  = onAllSelected,
                label    = { Text("Tous") },
            )
        }
        items(divisions) { div ->
            FilterChip(
                selected = div.id in selectedDivisionIds,
                onClick  = { onDivisionToggled(div.id) },
                label    = { Text(div.name) },
            )
        }
    }
}
