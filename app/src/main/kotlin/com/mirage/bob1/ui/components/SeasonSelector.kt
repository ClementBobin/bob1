package com.mirage.bob1.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mirage.bob1.domain.model.Season

/**
 * Season chips (newest first). [selected] is a season label, or `null` for the current season, which is what the API
 * answers when no season is sent. Hidden until there is more than one season to choose from.
 */
@Composable
fun SeasonSelector(
    seasons: List<Season>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (seasons.size < 2) return
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(seasons, key = { it.label }) { season ->
            val isSelected = if (selected == null) season.isCurrent else season.label == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (season.isCurrent) null else season.label) },
                label = { Text(if (season.isCurrent) "${season.label} · en cours" else season.label) },
            )
        }
    }
}
