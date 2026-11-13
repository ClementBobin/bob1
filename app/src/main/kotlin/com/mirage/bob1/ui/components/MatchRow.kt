package com.mirage.bob1.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.ui.core.formatTime

@Composable
fun MatchRow(match: Match, committed: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(match.startsAt.formatTime(), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.width(44.dp))
        Column(Modifier.weight(1f)) {
            Text(match.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        StatusBadge(match.statusLabel(committed), match.tone(committed))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
