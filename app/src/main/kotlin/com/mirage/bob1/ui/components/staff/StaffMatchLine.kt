package com.mirage.bob1.ui.components.staff

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
import com.mirage.bob1.domain.model.OpenStaffMatch
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.core.formatTime

@Composable
fun StaffMatchLine(m: OpenStaffMatch, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(m.startsAt.formatTime(), fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.width(44.dp))
        Column(Modifier.weight(1f)) {
            Text(m.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
        when {
            m.mine -> StatusBadge("Inscrit", Tone.Joined)
            m.isFull -> StatusBadge("Complet", Tone.Danger)
            else -> StatusBadge("${m.missing} poste${if (m.missing > 1) "s" else ""} libre${if (m.missing > 1) "s" else ""}", Tone.Pending)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
