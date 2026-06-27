package com.bob1.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.ui.core.components.ui.admin.*
import dev.kindling.compose.KScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    onNavigateToMatchManagement: () -> Unit = {}
) {
    KScreen(
        viewModel = viewModel<AdminViewModel>(),
        navController = navController,
        onEvent = { _, _, event ->
            when (event) {
                is AdminContracts.UiEvent.ShowError -> {
                    // Snackbar or toast would go here
                }
                is AdminContracts.UiEvent.SettingsSaved -> {
                    // Optional: show success message
                }
                else -> Unit
            }
        }
    ) { state, viewModel ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Administration", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                        }
                    }
                )
            }
        ) { paddingValues ->
            AdminContent(
                state = state,
                paddingValues = paddingValues,
                onShowSettingsEdit = viewModel::showSettingsEdit,
                onShowCreateDivision = viewModel::showCreateDivision,
                onDeleteDivision = viewModel::deleteDivision,
                onJ15Changed = viewModel::onJ15Changed,
                onJ4Changed = viewModel::onJ4Changed,
                onSaveSettings = viewModel::saveSettings,
                onDismissSettingsEdit = viewModel::dismissSettingsEdit,
                onDivisionNameChanged = viewModel::onDivisionNameChanged,
                onCreateDivision = viewModel::createDivision,
                onDismissCreateDivision = viewModel::dismissCreateDivision,
                onNavigateToMatchManagement = onNavigateToMatchManagement
            )
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun AdminContent(
    state: AdminContracts.UiState,
    paddingValues: PaddingValues,
    onShowSettingsEdit: () -> Unit = {},
    onShowCreateDivision: () -> Unit = {},
    onDeleteDivision: (String) -> Unit = {},
    onJ15Changed: (String) -> Unit = {},
    onJ4Changed: (String) -> Unit = {},
    onSaveSettings: () -> Unit = {},
    onDismissSettingsEdit: () -> Unit = {},
    onDivisionNameChanged: (String) -> Unit = {},
    onCreateDivision: () -> Unit = {},
    onDismissCreateDivision: () -> Unit = {},
    onNavigateToMatchManagement: () -> Unit = {}
) {
    if (state.isLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // ── Settings section ──────────────────────────────────────────
        item {
            SectionCard(
                title = "Paramètres",
                icon = Icons.Default.Settings,
                action = {
                    TextButton(onClick = onShowSettingsEdit) {
                        Text("Modifier")
                    }
                }
            ) {
                state.settings?.let { settings ->
                    InfoRow(
                        label = "Délai J-15",
                        value = "${settings.confirmationOffsetJ15} jours avant le match"
                    )
                    InfoRow(
                        label = "Délai J-4",
                        value = "${settings.confirmationOffsetJ4} jours avant le match"
                    )
                }
            }
        }

        // ── Divisions section ─────────────────────────────────────────
        item {
            SectionCard(
                title = "Divisions",
                icon = Icons.Default.Category,
                action = {
                    IconButton(onClick = onShowCreateDivision) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter une division")
                    }
                }
            ) {
                state.divisions.forEach { division ->
                    DivisionRow(
                        division = division,
                        onDelete = { onDeleteDivision(division.id) }
                    )
                }
                if (state.divisions.isEmpty()) {
                    Text(
                        "Aucune division",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // ── Point rules section ───────────────────────────────────────
        item {
            SectionCard(
                title = "Règles de points",
                icon = Icons.Default.EmojiEvents
            ) {
                state.pointRules.forEach { rule ->
                    InfoRow(
                        label = rule.role.displayName(),
                        value = "J-15: ${rule.pointsOnJ15}pts · J-4: ${rule.pointsOnJ4}pts · Urgence: ${rule.pointsEmergency}pts"
                    )
                }
            }
        }

        // ── Match management link ─────────────────────────────────────
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToMatchManagement
            ) {
                ListItem(
                    headlineContent = { Text("Gestion des matchs") },
                    supportingContent = { Text("Créer, modifier, supprimer des matchs") },
                    leadingContent = { Icon(Icons.Default.SportsBasketball, contentDescription = null) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) }
                )
            }
        }
    }

    // ── Dialogs ───────────────────────────────────────────────────────
    if (state.showSettingsEdit) {
        AlertDialog(
            onDismissRequest = onDismissSettingsEdit,
            title = { Text("Modifier les délais") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.editJ15,
                        onValueChange = onJ15Changed,
                        label = { Text("Jours avant match (J-15)") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.editJ4,
                        onValueChange = onJ4Changed,
                        label = { Text("Jours avant match (J-4)") },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onSaveSettings) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissSettingsEdit) {
                    Text("Annuler")
                }
            }
        )
    }

    if (state.showCreateDivision) {
        AlertDialog(
            onDismissRequest = onDismissCreateDivision,
            title = { Text("Nouvelle division") },
            text = {
                OutlinedTextField(
                    value = state.newDivisionName,
                    onValueChange = onDivisionNameChanged,
                    label = { Text("Nom (ex: U17)") },
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = onCreateDivision) {
                    Text("Créer")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissCreateDivision) {
                    Text("Annuler")
                }
            }
        )
    }
}