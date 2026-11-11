package com.mirage.bob1.ui.components.ranking

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mirage.bob1.domain.model.RankingRow
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import dev.kindling.core.components.ui.card.KCard
import dev.kindling.core.components.ui.card.KCardContent

@Composable
fun RankingRowCard(row: RankingRow, highlighted: Boolean) {
    KCard(Modifier.fillMaxWidth()) {
        KCardContent {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("#${row.rank}", fontWeight = FontWeight.ExtraBold, color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(40.dp))
                Column(Modifier.weight(1f)) {
                    Text(if (row.isMine) "${row.name} (vous)" else row.name, fontWeight = FontWeight.SemiBold)
                    row.subtitle?.let { Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                StatusBadge("${row.points} pts", if (row.points < 0) Tone.Danger else if (highlighted) Tone.Pending else Tone.Success)
            }
        }
    }
}
