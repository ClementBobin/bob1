package com.bob1.app.ui.screens.auth

import android.app.Application
import com.bob1.app.domain.repository.AuthRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object AuthContracts {
    data class UiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    sealed interface UiEvent {
        object LoginSuccess : UiEvent
        object RegisterSuccess : UiEvent
    }
}

class AuthViewModel(application: Application) : KViewModel<AuthContracts.UiState>(AuthContracts.UiState(), application) {
    private val repo: AuthRepository by inject()

    fun onEmailChanged(v: String)    = updateState { copy(email = v, error = null) }
    fun onPasswordChanged(v: String) = updateState { copy(password = v, error = null) }

    fun login() {
        val s = state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            updateState { copy(error = "Email et mot de passe requis.") }
            return
        }
        updateState { copy(isLoading = true, error = null) }
        fetchData(
            source = { repo.login(s.email.trim(), s.password) },
            onResult = { result ->
                result
                    .onSuccess { updateState { copy(isLoading = false) }; sendEvent(AuthContracts.UiEvent.LoginSuccess) }
                    .onFailure { updateState { copy(isLoading = false, error = it.message) } }
            }
        )
    }
}