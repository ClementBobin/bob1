package com.bob1.app.ui.screens.auth

import android.app.Application
import com.bob1.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import dev.kindling.core.components.KToastManager
import org.koin.core.component.inject

object AuthContracts {
    data class UiState(
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        val passwordHasMinLength get() = password.length >= 8
        val passwordHasUppercase get() = password.any { it.isUpperCase() }
        val passwordHasDigit     get() = password.any { it.isDigit() }
        val passwordHasSpecial   get() = password.any { !it.isLetterOrDigit() }
        val isPasswordStrong     get() = passwordHasMinLength && passwordHasUppercase && passwordHasDigit && passwordHasSpecial
    }
}

class AuthViewModel(application: Application) :
    KViewModel<AuthContracts.UiState>(AuthContracts.UiState(), application) {

    private val repo: AuthRepository by inject()

    fun onEmailChanged(v: String)    = updateState { copy(email = v, emailError = null) }
    fun onPasswordChanged(v: String) = updateState { copy(password = v, passwordError = null) }

    fun login(onSuccess: () -> Unit) {
        val s = state.value
        if (s.email.isBlank()) {
            updateState { copy(emailError = "Email et mot de passe requis.") }
            return
        }
        if (s.password.isBlank()) {
            updateState { copy(passwordError = "Email et mot de passe requis.") }
            return
        }
        updateState { copy(isLoading = true, emailError = null, passwordError = null) }
        fetchData(
            source   = { repo.login(s.email.trim(), s.password) },
            onResult = {
                onSuccess { updateState { copy(isLoading = false) } }
                onFailure { e -> updateState { copy(isLoading = false, error = e.message) } }
                onSuccess()
            }
        )
    }

    /** Valide les champs puis crée le compte. [onSuccess] navigue vers LoginScreen. */
    fun register(onSuccess: () -> Unit) {
        val s = state.value

        val emailError     = when {
            s.email.isBlank() -> "L'adresse e-mail est obligatoire"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "Adresse e-mail invalide"
            else -> null
        }
        val passwordError  = if (!s.isPasswordStrong) "Le mot de passe ne respecte pas les critères" else null

        if (listOfNotNull(emailError, passwordError).isNotEmpty()) {
            updateState { copy(emailError = emailError, passwordError = passwordError) }
            KToastManager.warning("Veuillez corriger les erreurs avant de continuer")
            return
        }

        fetchData(
            source = {
                repo.register(
                    registerRequest = mapOf(
                        "email"    to s.email.trim(),
                        "password" to s.password
                    )
                )
            },
            onResult = {
                onSuccess {
                    updateState { copy(isLoading = false) }
                    KToastManager.success("Compte créé !", "Connectez-vous pour accéder à votre espace.")
                    onSuccess()
                }
                onFailure {
                    updateState { copy(isLoading = false) }
                }
            }
        )
        updateState { copy(isLoading = true) }
    }
}