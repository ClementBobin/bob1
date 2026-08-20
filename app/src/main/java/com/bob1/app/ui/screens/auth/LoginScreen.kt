package com.bob1.app.ui.screens.auth

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.ui.core.Destination
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.KButtonVariant
import dev.kindling.core.components.ui.KButton
import org.koin.compose.koinInject

@Composable
fun LoginScreen(navController: NavController) {
    val activity  = LocalActivity.current as? FragmentActivity
    val biometric = koinInject<BiometricHelper>()

    KScreen(
        viewModel     = viewModel<AuthViewModel>(),
        navController = navController,
        onEvent = { _, vm, event ->
            when (event) {
                is AuthContracts.UiEvent.LoginSuccess ->
                    navController.navigate(Destination.Calendar.route) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }

                is AuthContracts.UiEvent.LaunchBiometric ->
                    activity?.let {
                        biometric.authenticate(
                            activity = it,
                            config   = vm.biometricConfig,
                            onResult = { result -> vm.onBiometricResult(result) },
                        )
                    }

                else -> Unit
            }
        }
    ) { state, vm ->
        LoginContent(
            state              = state,
            onEmailChanged     = vm::onEmailChange,
            onPasswordChanged  = vm::onPasswordChange,
            onLogin            = vm::login,
            onBiometricTap     = {
                activity?.let {
                    biometric.authenticate(
                        activity = it,
                        config   = vm.biometricConfig,
                        onResult = vm::onBiometricResult,
                    )
                }
            },
            onShowFallback     = vm::showPasswordFallback,
            onNavigateRegister = { navController.navigate(Destination.Register.route) },
        )
    }
}

@Composable
private fun LoginContent(
    state: AuthContracts.UiState,
    onEmailChanged: (String) -> Unit    = {},
    onPasswordChanged: (String) -> Unit = {},
    onLogin: () -> Unit                 = {},
    onBiometricTap: () -> Unit          = {},
    onShowFallback: () -> Unit          = {},
    onNavigateRegister: () -> Unit      = {},
) {
    val cs = MaterialTheme.colorScheme
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = cs.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(Icons.Default.SportsBasketball, null,
                modifier = Modifier.size(56.dp), tint = cs.primary)
            Spacer(Modifier.height(8.dp))
            Text("BOB1", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Gestion des officiels", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            Spacer(Modifier.height(40.dp))

            // ── Biometric primary view ────────────────────────────────────────
            // Shown when hardware is present, user has opted in, and credentials exist.
            AnimatedVisibility(
                visible = state.biometricAvailable && state.biometricEnabled && !state.showPasswordFallback,
                enter = fadeIn(), exit = fadeOut(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Default.Fingerprint, null,
                        modifier = Modifier.size(72.dp), tint = cs.primary)
                    Text("Authentification biométrique",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Utilisez votre empreinte ou Face ID pour vous connecter.",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant, textAlign = TextAlign.Center,
                    )
                    state.biometricError?.let { err ->
                        Text(err, color = cs.error, style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center)
                    }
                    if (state.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    } else {
                        KButton(text = "Déverrouiller", onClick = onBiometricTap,
                            modifier = Modifier.fillMaxWidth())
                    }
                    TextButton(onClick = onShowFallback) {
                        Text("Utiliser mon mot de passe", fontSize = 13.sp)
                    }
                }
            }

            // ── Password fallback ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.showPasswordFallback || !state.biometricAvailable || !state.biometricEnabled,
                enter = fadeIn(), exit = fadeOut(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        if (state.biometricAvailable && state.biometricEnabled)
                            "Connexion par mot de passe"
                        else
                            "Connexion",
                        style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    )
                    OutlinedTextField(
                        value = state.email, onValueChange = onEmailChanged,
                        label = { Text("Email") }, leadingIcon = { Icon(Icons.Default.Email, null) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(), isError = state.error != null,
                    )
                    OutlinedTextField(
                        value = state.password, onValueChange = onPasswordChanged,
                        label = { Text("Mot de passe") }, leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true, modifier = Modifier.fillMaxWidth(), isError = state.error != null,
                    )
                    state.error?.let { Text(it, color = cs.error, style = MaterialTheme.typography.bodySmall) }
                    KButton(text = "Se connecter", onClick = onLogin, isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth())

                    // Only show biometric shortcut if enabled
                    if (state.biometricAvailable && state.biometricEnabled) {
                        KButton(onClick = onBiometricTap, modifier = Modifier.fillMaxWidth(),
                            variant = KButtonVariant.Ghost) {
                            Icon(Icons.Default.Fingerprint, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Utiliser la biométrie")
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onNavigateRegister) { Text("Pas encore de compte ? S'inscrire") }
        }
    }
}