package com.mirage.bob1.ui.screens.profile

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.usecase.CreateFamilyUseCase
import com.mirage.bob1.domain.usecase.CreateInvitationUseCase
import com.mirage.bob1.domain.usecase.DisableBiometricUseCase
import com.mirage.bob1.domain.usecase.EnableBiometricUseCase
import com.mirage.bob1.domain.usecase.GetFamilyUseCase
import com.mirage.bob1.domain.usecase.GetMeUseCase
import com.mirage.bob1.domain.usecase.JoinFamilyUseCase
import com.mirage.bob1.domain.usecase.LeaveFamilyUseCase
import com.mirage.bob1.domain.usecase.LogoutUseCase
import com.mirage.bob1.domain.usecase.ObserveBiometricStatusUseCase
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.core.AppModeStore
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.ShareText
import com.mirage.bob1.ui.screens.register.INVITATION_CODE_LENGTH
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
    private val createFamily: CreateFamilyUseCase,
    private val joinFamily: JoinFamilyUseCase,
    private val leaveFamily: LeaveFamilyUseCase,
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
        refresh()
    }

    /** The person, then their family (only when they have one). Also run after every family change. */
    private fun refresh() {
        call({ getMe() }) { u ->
            updateState { copy(user = u) }
            if (u.familyId != null) call({ getFamily() }, onError = {}) { f -> updateState { copy(family = f) } }
            else updateState { copy(family = null) }
        }
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

    /** A new code (parents only), shown in a dialog from which it can be shared. */
    fun invite() {
        updateState { copy(inviting = true) }
        call({ createInvitation() }, onError = { updateState { copy(inviting = false) }; sendError(it) }) { inv ->
            updateState { copy(inviting = false, invitation = inv) }
        }
    }

    fun shareInvitation() {
        val inv = state.value.invitation ?: return
        updateState { copy(invitation = null) }
        sendEvent(ShareText("Rejoins ma famille sur BOB1 avec le code ${inv.code} : à l'inscription (« J'ai un code ») ou depuis Profil → Rejoindre avec un code. Valable 7 jours, une seule utilisation."))
    }

    fun dismissInvitation() = updateState { copy(invitation = null) }

    fun openFamilyDialog(dialog: FamilyDialog) = updateState { copy(familyDialog = dialog, familyName = "", joinCode = "") }
    fun closeFamilyDialog() = updateState { copy(familyDialog = null) }
    fun onFamilyName(v: String) = updateState { copy(familyName = v) }
    fun onJoinCode(v: String) = updateState { copy(joinCode = v.filter { it.isLetterOrDigit() }.uppercase().take(INVITATION_CODE_LENGTH)) }

    /** Create / join / leave, depending on the open dialog. The API renews the session (the family id is in the token). */
    fun confirmFamilyDialog() {
        val s = state.value
        val dialog = s.familyDialog ?: return
        if (s.familyBusy || (dialog == FamilyDialog.Join && !s.joinCodeValid)) return
        updateState { copy(familyBusy = true) }
        call(
            block = {
                when (dialog) {
                    FamilyDialog.Create -> createFamily(s.familyName.trim().ifBlank { null })
                    FamilyDialog.Join -> joinFamily(s.joinCode)
                    FamilyDialog.Leave -> leaveFamily()
                }
            },
            onError = { updateState { copy(familyBusy = false) }; sendError(it) },
            onOk = {
                updateState { copy(familyBusy = false, familyDialog = null) }
                sendInfo(when (dialog) {
                    FamilyDialog.Create -> "Famille créée"
                    FamilyDialog.Join -> "Vous avez rejoint la famille"
                    FamilyDialog.Leave -> "Vous avez quitté la famille"
                })
                refresh()
            },
        )
    }

    fun logout() {
        updateState { copy(loggingOut = true) }
        viewModelScope.launch { logoutUseCase() } // session flips -> AppRoot goes back to login
    }
}
