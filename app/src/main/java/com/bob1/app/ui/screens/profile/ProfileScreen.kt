package com.bob1.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.data.dto.UserRole
import com.bob1.app.ui.core.Destination
import com.bob1.app.ui.core.components.ui.AppBottomBar
import dev.kindling.compose.KScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    KScreen(
        viewModel     = viewModel<ProfileViewModel>(),
        navController = navController,
        onEvent = { _, _, event ->
            if (event is ProfileContracts.UiEvent.LoggedOut) {
                navController.navigate(Destination.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    ) { state, vm ->
        Scaffold(
            topBar    = { TopAppBar(title = { Text("Profil", fontWeight = FontWeight.Bold) }) },
            bottomBar = { AppBottomBar(navController) }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().padding(padding).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.size(80.dp).clip(CircleShape),
                    color    = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null,
                            modifier = Modifier.size(48.dp),
                            tint     = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Spacer(Modifier.height(16.dp))
                state.user?.let { user ->
                    Text("${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    AssistChip(
                        onClick = {},
                        label   = { Text(if (user.role == UserRole.ADMIN) "Administrateur" else "Officiel") },
                        leadingIcon = {
                            Icon(if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings
                            else Icons.Default.SportsMartialArts, null,
                                modifier = Modifier.size(16.dp))
                        }
                    )
                }
                Spacer(Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                if (state.user?.role == UserRole.ADMIN) {
                    ListItem(
                        headlineContent = { Text("Administration") },
                        leadingContent  = { Icon(Icons.Default.AdminPanelSettings, null) },
                        trailingContent = { Icon(Icons.Default.ChevronRight, null) },
                        modifier        = Modifier.clickable { navController.navigate(Destination.Admin.route) },
                    )
                    HorizontalDivider()
                }
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick  = vm::logout,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Se déconnecter")
                }
            }
        }
    }
}