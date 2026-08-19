package com.bob1.app.ui.core.components.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bob1.app.data.dto.OfficialRole
import com.bob1.app.domain.model.Match
import com.bob1.app.domain.usecase.NavigationHelper
import com.bob1.app.domain.usecase.dotColor
import com.bob1.app.domain.usecase.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailSheet(
    match: Match,
    currentUserId: String?,
    onRoleTap: (OfficialRole) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current

    val isLoadingFullMatch = match.homeTeam.name.isBlank()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {

            // ── Status badge ──────────────────────────────────────────────
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

            // ── Title — shows spinner until full MatchDto is loaded ────────
            if (isLoadingFullMatch) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(
                    "${match.homeTeam.name}  vs  ${match.awayTeam.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            // ── Division + date ───────────────────────────────────────────
            Text(
                match.divisionName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                match.dateIso.let {
                    "${it.substring(8, 10)}/${it.substring(5, 7)}/${it.substring(0, 4)} ${it.substring(11, 16)}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            // ── Location row with navigation icon ─────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { NavigationHelper.launch(context, match) }
                    .padding(vertical = 6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Ouvrir dans l'application de navigation",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Column {
                    Text(
                        match.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                    if (match.locationAddress.isNotBlank()) {
                        Text(
                            match.locationAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Role slots ────────────────────────────────────────────────
            Text("Rôles", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            match.slots.forEach { slot ->
                val isCurrentUser  = currentUserId != null && slot.assignedUserId == currentUserId
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
                        modifier = Modifier.weight(1f),
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
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}