package com.bob1.app.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.bob1.app.ui.core.Destination
import com.bob1.app.ui.core.components.ui.AuthCard
import com.bob1.app.ui.core.components.ui.FieldWithLabel
import com.bob1.app.ui.core.components.ui.PasswordCriterionRow
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.KButton

@Composable
fun RegisterScreen(
    navController: NavController,
) {
    KScreen(
        viewModel = viewModel<AuthViewModel>(),
        navController = navController,
        onEvent = { _, _, event ->
            when (event) {
                is AuthContracts.UiEvent.RegisterSuccess -> navController.navigate(Destination.Calendar.route) {
                    popUpTo(Destination.Register.route) { inclusive = true }
                }
                else -> Unit
            }
        }
    ) { state, vm ->
        RegisterContent(
            state = state,
            onFirstNameChange = vm::onFirstNameChange,
            onLastNameChange = vm::onLastNameChange,
            onEmailChange = vm::onEmailChange,
            onPasswordChange = vm::onPasswordChange,
            onRegister = vm::register,
            onNavigateToLogin = { navController.navigate(Destination.Login.route) }
        )
    }
}

@Composable
private fun RegisterContent(
    state: AuthContracts.UiState = AuthContracts.UiState(),
    onFirstNameChange: (String) -> Unit = {},
    onLastNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    var showPasswordRules by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme

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
            AuthCard {

                // Title
                Text(
                    text = "Créer un compte",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Subtitle with login link
                val subtitle = buildAnnotatedString {
                    append("Vous avez déjà un compte ? ")
                    withStyle(SpanStyle(color = cs.primary, fontWeight = FontWeight.SemiBold)) {
                        append(" Connectez-vous")
                    }
                }
                TextButton(
                    onClick = onNavigateToLogin,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(subtitle, fontSize = 14.sp, color = cs.onSurfaceVariant)
                }

                Spacer(Modifier.height(24.dp))

                // First name + Last name side by side (matches web grid)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FieldWithLabel(
                            label = "Prénom",
                            value = state.firstName,
                            onValueChange = onFirstNameChange,
                            placeholder = "Entrez votre prénom",
                            isError = state.firstNameError != null,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.firstNameError != null) {
                            Text(
                                state.firstNameError,
                                color = cs.error,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FieldWithLabel(
                            label = "Nom",
                            value = state.lastName,
                            onValueChange = onLastNameChange,
                            placeholder = "Entrez votre nom",
                            isError = state.lastNameError != null,
                            enabled = !state.isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (state.lastNameError != null) {
                            Text(
                                state.lastNameError,
                                color = cs.error,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

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
                        state.emailError,
                        color = cs.error,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 3.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Password with focus → show rules
                FieldWithLabel(
                    label = "Mot de passe",
                    value = state.password,
                    onValueChange = {
                        onPasswordChange(it)
                        if (it.isNotEmpty()) showPasswordRules = true
                    },
                    placeholder = "••••••••",
                    isPassword = true,
                    isError = state.passwordError != null,
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                // Password strength rules
                AnimatedVisibility(visible = showPasswordRules && state.password.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = cs.surfaceVariant
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PasswordCriterionRow(
                                label = "Au moins 8 caractères",
                                met = state.passwordHasMinLength
                            )
                            PasswordCriterionRow(
                                label = "Au moins une lettre majuscule",
                                met = state.passwordHasUppercase
                            )
                            PasswordCriterionRow(
                                label = "Au moins un chiffre",
                                met = state.passwordHasDigit
                            )
                            PasswordCriterionRow(
                                label = "Au moins un caractère spécial",
                                met = state.passwordHasSpecial
                            )
                        }
                    }
                }

                // General error
                AnimatedVisibility(visible = state.passwordError != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = cs.errorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text(
                            state.passwordError ?: "",
                            fontSize = 13.sp,
                            color = cs.error,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Submit
                KButton(
                    text = "S'inscrire",
                    onClick = onRegister,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}