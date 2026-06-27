package com.bob1.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.R
import com.bob1.app.ui.core.components.ui.AuthCard
import com.bob1.app.ui.core.components.ui.FieldWithLabel
import dev.kindling.compose.KScreen
import dev.kindling.core.components.KButton

@Composable
fun LoginScreen(
    navController: NavController,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    KScreen(
        viewModel = viewModel<AuthViewModel>(),
        navController = navController
    ) { state, vm ->
        LoginContent(
            state = state,
            onEmailChange = vm::onEmailChanged,
            onPasswordChange = vm::onPasswordChanged,
            onLogin = { vm.login(onLoginSuccess) },
            onNavigateToRegister = onNavigateToRegister,
        )
    }
}

@Composable
private fun LoginContent(
    state: AuthContracts.UiState = AuthContracts.UiState(),
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    var rememberMe by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme

    // Full-screen background follows theme (light/dark)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = cs.background) {}

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card — surface color follows theme
            AuthCard {

                // Lock icon in primary-tinted rounded square
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = cs.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = cs.onPrimaryContainer,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Title
                Text(
                    text = "Bon retour",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle with register link
                val subtitle = buildAnnotatedString {
                    append("Ou ")
                    withStyle(SpanStyle(color = cs.primary, fontWeight = FontWeight.SemiBold)) {
                        append("créez un compte")
                    }
                }
                TextButton(
                    onClick = onNavigateToRegister,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(subtitle, fontSize = 14.sp, color = cs.onSurfaceVariant)
                }

                Spacer(Modifier.height(24.dp))

                // Error banner
                AnimatedVisibility(visible = state.emailError != null || state.passwordError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = cs.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.emailError ?: state.passwordError ?: "",
                            fontSize = 13.sp,
                            color = cs.error,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                // Email
                FieldWithLabel(
                    label = "Adresse e-mail",
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = "Entrez votre e-mail",
                    isError = state.emailError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.emailError != null) {
                    Text(
                        text = state.emailError,
                        color = cs.error,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Password
                FieldWithLabel(
                    label = "Mot de passe",
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = "••••••••",
                    isPassword = true,
                    isError = state.passwordError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                // Submit button
                KButton(
                    text = "Se connecter",
                    onClick = onLogin,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}