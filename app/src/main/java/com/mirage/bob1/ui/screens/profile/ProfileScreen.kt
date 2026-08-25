package com.mirage.bob1ob1.ui.screens.profile

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mirage.bob1ob1.data.dto.UserRole
import com.mirage.bob1ob1.ui.core.Destination
import com.mirage.bob1ob1.ui.core.components.ui.AppBottomBar
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.android.natif.BiometricResult
import dev.kindling.compose.KScreen
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val activity  = LocalActivity.current as? FragmentActivity
    val biometric = koinInject<BiometricHelper>()

    KScreen(
        viewModel     = viewModel<ProfileViewModel>(),
        navController = navController,
        onEvent = { _, vm, event ->
            when (event) {
                is ProfileContracts.UiEvent.LoggedOut -> {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is ProfileContracts.UiEvent.ConfirmBiometricEnable -> {
                    activity?.let {
                        biometric.authenticate(
                            activity = it,
                            config   = vm.biometricConfig,
                            onResult = { result ->
                                when (result) {
                                    BiometricResult.Success,
                                    is BiometricResult.SuccessWithEncrypted,
                                    is BiometricResult.SuccessWithDecrypted -> vm.onBiometricEnableConfirmed()
                                    else -> vm.onBiometricEnableCancelled()
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { state, vm ->
        Scaffold(
            topBar    = { TopAppBar(title = { Text("Profil", fontWeight = FontWeight.Bold) }) },
            bottomBar = { AppBottomBar(navController) }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(16.dp))

                // ── Avatar ────────────────────────────────────────────────────
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

                // ── User info ─────────────────────────────────────────────────
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
                            Icon(
                                if (user.role == UserRole.ADMIN) Icons.Default.AdminPanelSettings
                                else Icons.Default.SportsMartialArts,
                                null, modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }

                Spacer(Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(Modifier.height(24.dp))

                // ── Biometric toggle ──────────────────────────────────────────
                if (state.biometricAvailable) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.Fingerprint,
                                contentDescription = null,
                                tint     = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Connexion biométrique",
                                    style      = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                )
                                Text(
                                    if (state.biometricEnabled)
                                        "Activée — vous pouvez vous connecter par empreinte / Face ID"
                                    else
                                        "Désactivée — activez pour vous connecter sans mot de passe",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked         = state.biometricEnabled,
                                onCheckedChange = vm::onBiometricToggle,
                            )
                        }
                    }

                    // Status / feedback message
                    AnimatedVisibility(
                        visible = state.biometricStatusMessage != null,
                        enter   = fadeIn(),
                        exit    = fadeOut(),
                    ) {
                        state.biometricStatusMessage?.let { msg ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.biometricEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            LaunchedEffect(msg) {
                                kotlinx.coroutines.delay(3_000)
                                vm.dismissStatusMessage()
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                Spacer(Modifier.weight(1f))

                // ── Logout ────────────────────────────────────────────────────
                OutlinedButton(
                    onClick  = vm::logout,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Se déconnecter")
                }
            }
        }
    }
}