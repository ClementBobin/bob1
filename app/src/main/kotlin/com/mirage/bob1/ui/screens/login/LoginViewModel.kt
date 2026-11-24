package com.mirage.bob1.ui.screens.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.usecase.LoginWithBiometricUseCase
import com.mirage.bob1.domain.usecase.ObserveBiometricStatusUseCase
import com.mirage.bob1.domain.usecase.LoginUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.RegisterDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    application: Application,
    private val login: LoginUseCase,
    private val loginWithBiometric: LoginWithBiometricUseCase,
    observeBiometricStatus: ObserveBiometricStatusUseCase,
) : Bob1ViewModel<LoginState>(LoginState(), application) {

    init {
        viewModelScope.launch { observeBiometricStatus().collect { s -> updateState { copy(biometricReady = s.canSignIn) } } }
    }

    fun onEmail(value: String) = updateState { copy(email = value) }
    fun onPassword(value: String) = updateState { copy(password = value) }

    fun openRegister() = navigate(RegisterDestination)

    /** Fingerprint / face prompt, then a normal session; success flips the session and AppRoot swaps to the main graph. */
    fun biometricLogin(activity: FragmentActivity) {
        if (state.value.loading) return
        updateState { copy(loading = true) }
        call(
            block = { loginWithBiometric(activity) },
            onError = { updateState { copy(loading = false) }; sendError(it) },
            onOk = { updateState { copy(loading = false) } },
        )
    }

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
