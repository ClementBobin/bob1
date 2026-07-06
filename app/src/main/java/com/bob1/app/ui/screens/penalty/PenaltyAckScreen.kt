package com.bob1.app.ui.screens.penalty

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.ui.core.Destination
import dev.kindling.compose.KScreen
import dev.kindling.core.components.KButton

@Composable
fun PenaltyAckScreen(navController: NavController) {
    KScreen(
        viewModel     = viewModel<PenaltyAckViewModel>(),
        navController = navController,
        onEvent = { _, _, event ->
            if (event is PenaltyAckContracts.UiEvent.AllAcknowledged) {
                navController.navigate(Destination.Calendar.route) {
                    popUpTo(Destination.PenaltyAck.route) { inclusive = true }
                }
            }
        }
    ) { state, vm ->
        val penalty = state.current ?: return@KScreen
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Default.Warning, null, modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
                Text("Pénalité reçue", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(24.dp))
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (penalty.kickedOut) {
                            Text("Vous avez été exclu d'un match", fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                        LabelValue("Motif", penalty.reason)
                        LabelValue("Points retirés", "-${penalty.points} pts")
                        if (!penalty.matchId.isNullOrBlank()) LabelValue("Match", penalty.matchId)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Contactez l'administration si vous souhaitez contester cette sanction.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                if (state.pending.size > 1) {
                    Spacer(Modifier.height(8.dp))
                    Text("${state.currentIndex + 1} / ${state.pending.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(32.dp))
                KButton(text = if (state.hasMore) "Suivant" else "J'ai compris",
                    onClick = vm::acknowledge, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Row {
        Text("$label : ", fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onErrorContainer)
        Text(value, color = MaterialTheme.colorScheme.onErrorContainer)
    }
}