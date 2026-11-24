package com.mirage.bob1.ui.screens.profile

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.usecase.CreateInvitationUseCase
import com.mirage.bob1.domain.usecase.DisableBiometricUseCase
import com.mirage.bob1.domain.usecase.EnableBiometricUseCase
import com.mirage.bob1.domain.usecase.GetFamilyUseCase
import com.mirage.bob1.domain.usecase.GetMeUseCase
import com.mirage.bob1.domain.usecase.LogoutUseCase
import com.mirage.bob1.domain.usecase.ObserveBiometricStatusUseCase
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.core.AppModeStore
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.ShareText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.bob1.capacity.permission.NotificationPermission

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val getMe: GetMeUseCase,
    private val getFamily: GetFamilyUseCase,
    private val createInvitation: CreateInvitationUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val permission: NotificationPermission,
    private val modeStore: AppModeStore,
    private val enableBiometric: EnableBiometricUseCase,
    private val disableBiometric: DisableBiometricUseCase,
    observeBiometricStatus: ObserveBiometricStatusUseCase,
) : Bob1ViewModel<ProfileState>(ProfileState(), application) {

    init {
        viewModelScope.launch { modeStore.mode.collect { m -> updateState { copy(mode = m) } } }
        viewModelScope.launch { permission.granted.collect { g -> updateState { copy(notificationsEnabled = g) } } }
        viewModelScope.launch {
            observeBiometricStatus().collect { s -> updateState { copy(biometricAvailable = s.available || s.enrolled, biometricEnabled = s.enrolled) } }
        }
        call({ getMe() }) { u -> updateState { copy(user = u) } }
        call({ getFamily() }, onError = {}) { f -> updateState { copy(family = f) } }
    }

    fun enableNotifications() = permission.request()
    fun setMode(mode: AppMode) = modeStore.set(mode)

    /** Turns biometric sign-in on (fingerprint / face prompt to lock the device token) or off for this device. */
    fun toggleBiometric(activity: FragmentActivity) {
        if (state.value.biometricBusy) return
        val enabling = !state.value.biometricEnabled
        updateState { copy(biometricBusy = true) }
        call(
            block = { if (enabling) enableBiometric(activity) else disableBiometric() },
            onError = { updateState { copy(biometricBusy = false) }; sendError(it) },
            onOk = {
                updateState { copy(biometricBusy = false) }
                sendInfo(if (enabling) "Connexion biométrique activée" else "Connexion biométrique désactivée")
            },
        )
    }

    fun invite() {
        updateState { copy(inviting = true) }
        call({ createInvitation() }, onError = { updateState { copy(inviting = false) }; sendError(it) }) { inv ->
            updateState { copy(inviting = false) }
            sendEvent(ShareText("Rejoins ma famille sur BOB1 : crée ton compte, choisis « J'ai un code » et saisis ${inv.code} (valable 7 jours)."))
        }
    }

    fun logout() {
        updateState { copy(loggingOut = true) }
        viewModelScope.launch { logoutUseCase() } // session flips -> AppRoot goes back to login
    }
}
