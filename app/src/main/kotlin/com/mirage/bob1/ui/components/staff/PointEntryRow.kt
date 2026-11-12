package com.mirage.bob1.ui.components.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.PointEntry
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.core.formatShort

@Composable
fun PointEntryRow(e: PointEntry) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(e.reason.ifBlank { e.category }, fontWeight = FontWeight.SemiBold, maxLines = 2)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(e.createdAt.formatShort(), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (e.isManual) StatusBadge("Manuel", Tone.Neutral)
                if (e.isRevoked) StatusBadge("Révoqué", Tone.Danger)
            }
        }
        // A revoked adjustment no longer counts: show it greyed out.
        val tone = when {
            e.isRevoked -> Tone.Neutral
            e.points < 0 -> Tone.Danger
            else -> Tone.Success
        }
        StatusBadge(if (e.points > 0) "+${e.points} pts" else "${e.points} pts", tone)
    }
}
