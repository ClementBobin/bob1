package com.bob1.app.ui.core.components.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bob1.app.data.dto.OfficialRole
import com.bob1.app.domain.model.Match
import com.bob1.app.domain.usecase.dotColor
import com.bob1.app.domain.usecase.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailSheet(
    match: Match,
    onRoleTap: (OfficialRole) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(match.subscriptionStatus.dotColor())
                )
                Spacer(Modifier.width(8.dp))
                Text(match.subscriptionStatus.label(), style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${match.homeTeam.name}  vs  ${match.awayTeam.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "${match.divisionName} · ${match.location}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                match.dateIso.let { "${it.substring(8, 10)}/${it.substring(5, 7)}/${it.substring(0, 4)} ${it.substring(11, 16)}" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))
            Text("Rôles", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            match.slots.forEach { slot ->
                val isCurrentUser  = slot.assignedUserId == "u-official" // simplified
                val isTakenByOther = slot.assignedUserId != null && !isCurrentUser
                val isFree         = slot.assignedUserId == null
                val bgColor = when {
                    isCurrentUser  -> MaterialTheme.colorScheme.primaryContainer
                    isTakenByOther -> MaterialTheme.colorScheme.surfaceVariant
                    else           -> MaterialTheme.colorScheme.surface
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .then(
                            if (isFree || isCurrentUser)
                                Modifier.clickable { onRoleTap(slot.role) }
                            else Modifier
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        slot.role.displayName(),
                        modifier   = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        slot.assignedUserName ?: "Vacant",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isFree) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (isCurrentUser) {
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}