package com.bob1.app.ui.screens.auth

import android.app.Application
import com.bob1.app.data.local.SessionManager
import com.bob1.app.domain.repository.AuthRepository
import dev.kindling.android.natif.BiometricConfig
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.android.natif.BiometricResult
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object AuthContracts {
    data class UiState(
        val firstName: String = "",
        val firstNameError: String? = null,
        val lastName: String = "",
        val lastNameError: String? = null,
        val email: String = "",
        val emailError: String? = null,
        val password: String = "",
        val passwordError: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val biometricAvailable: Boolean = false,
        val showPasswordFallback: Boolean = false,
        val biometricError: String? = null,
    ) {
        val passwordHasMinLength get() = password.length >= 8
        val passwordHasUppercase get() = password.any { it.isUpperCase() }
        val passwordHasDigit     get() = password.any { it.isDigit() }
        val passwordHasSpecial   get() = password.any { !it.isLetterOrDigit() }
        val isPasswordStrong     get() = passwordHasMinLength && passwordHasUppercase && passwordHasDigit && passwordHasSpecial
    }

    sealed interface UiEvent {
        object LoginSuccess    : UiEvent
        object RegisterSuccess : UiEvent
        object LaunchBiometric : UiEvent
    }
}

class AuthViewModel(application: Application) :
    KViewModel<AuthContracts.UiState>(AuthContracts.UiState(), application) {

    private val repo: AuthRepository       by inject()
    private val session: SessionManager    by inject()
    private val biometric: BiometricHelper by inject()

    private val biometricConfig = BiometricConfig.strongOrPin(
        title    = "bob1",
        subtitle = "Connectez-vous avec votre biométrie",
    )

    init { checkBiometric() }

    private fun checkBiometric() {
        val available  = biometric.canAuthenticate(biometricConfig)
        val hasSession = session.isAuthenticated()
        updateState {
            copy(
                biometricAvailable   = available,
                showPasswordFallback = !available || !hasSession,
            )
        }
        if (hasSession && available) sendEvent(AuthContracts.UiEvent.LaunchBiometric)
    }

    fun onBiometricResult(result: BiometricResult) {
        when (result) {
            BiometricResult.Success      -> sendEvent(AuthContracts.UiEvent.LoginSuccess)
            is BiometricResult.Error     -> updateState { copy(biometricError = result.message, showPasswordFallback = true) }
            BiometricResult.Failed       -> updateState { copy(biometricError = "Biométrie non reconnue. Réessayez ou utilisez votre mot de passe.") }
            BiometricResult.NoneEnrolled -> updateState { copy(biometricAvailable = false, showPasswordFallback = true) }
            BiometricResult.Unavailable  -> updateState { copy(biometricAvailable = false, showPasswordFallback = true) }
        }
    }

    fun showPasswordFallback() = updateState { copy(showPasswordFallback = true, biometricError = null) }

    fun onFirstNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.isBlank()) "Prénom invalide" else null
        updateState { copy(firstName = v, firstNameError = error) }
    }

    fun onLastNameChange(v: String) {
        val error = if (v.isNotEmpty() && v.isBlank()) "Nom invalide" else null
        updateState { copy(lastName = v, lastNameError = error) }
    }

    fun onEmailChange(v: String) {
        val error = if (v.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(v).matches())
            "Adresse e-mail invalide" else null
        updateState { copy(email = v, emailError = error) }
    }

    fun onPasswordChange(v: String) {
        updateState { copy(password = v, passwordError = null) }
    }

    fun login() {
        val s = state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            updateState { copy(error = "Email et mot de passe requis.") }
            return
        }
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source   = { repo.login(s.email.trim(), s.password) },
            onResult = {
                onSuccess { updateState { copy(isLoading = false) }; sendEvent(AuthContracts.UiEvent.LoginSuccess) }
                onFailure { e -> updateState { copy(isLoading = false, error = e.message) } }
            }
        )
    }

    fun register() {
        val s = state.value
        val firstNameError = if (s.firstName.isBlank()) "Le prénom est obligatoire" else null
        val lastNameError  = if (s.lastName.isBlank()) "Le nom est obligatoire" else null
        val emailError     = when {
            s.email.isBlank() -> "L'adresse e-mail est obligatoire"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches() -> "Adresse e-mail invalide"
            else -> null
        }
        val passwordError  = if (!s.isPasswordStrong) "Le mot de passe ne respecte pas les critères" else null
        if (listOfNotNull(firstNameError, lastNameError, emailError, passwordError).isNotEmpty()) {
            updateState { copy(firstNameError = firstNameError, lastNameError = lastNameError, emailError = emailError, passwordError = passwordError) }
            return
        }
        fetchData(
            source   = { repo.register(s.firstName.trim(), s.lastName.trim(), s.email.trim(), s.password).getOrThrow() },
            onResult = {
                onSuccess { updateState { copy(isLoading = false) }; sendEvent(AuthContracts.UiEvent.RegisterSuccess) }
                onFailure { e -> updateState { copy(isLoading = false, error = e.message) } }
            }
        )
    }
}