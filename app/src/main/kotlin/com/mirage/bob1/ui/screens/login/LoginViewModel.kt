package com.mirage.bob1.ui.screens.login

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.mirage.bob1.domain.usecase.LoginUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.RegisterDestination

data class LoginState(val email: String = "", val password: String = "", val loading: Boolean = false) {
    val canSubmit get() = email.isNotBlank() && password.isNotBlank() && !loading
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val login: LoginUseCase,
) : Bob1ViewModel<LoginState>(LoginState(), application) {

    fun onEmail(value: String) = updateState { copy(email = value) }
    fun onPassword(value: String) = updateState { copy(password = value) }

    fun openRegister() = navigate(RegisterDestination)

    fun submit() {
        val s = state.value
        if (!s.canSubmit) return
        updateState { copy(loading = true) }
        call(
            block = { login(s.email, s.password) },
            onError = { updateState { copy(loading = false) }; sendError(it) },
            // Success: the session flips to "authenticated" and AppRoot swaps to the main graph by itself.
            onOk = { updateState { copy(loading = false) } },
        )
    }
}
