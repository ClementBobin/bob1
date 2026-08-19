package com.bob1.app.ui.screens.profile

import android.app.Application
import com.bob1.app.data.local.SessionManager
import com.bob1.app.domain.model.User
import dev.kindling.android.natif.BiometricConfig
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object ProfileContracts {
    data class UiState(
        val user: User? = null,
        val biometricAvailable: Boolean = false,
        val biometricEnabled: Boolean = false,
        val showBiometricEnableDialog: Boolean = false,
        val biometricStatusMessage: String? = null,
    )

    sealed interface UiEvent {
        object LoggedOut : UiEvent
        /** Ask the UI layer to show a biometric prompt to confirm enrollment. */
        object ConfirmBiometricEnable : UiEvent
    }
}

class ProfileViewModel(application: Application) :
    KViewModel<ProfileContracts.UiState>(ProfileContracts.UiState(), application) {

    private val session: SessionManager    by inject()
    private val biometric: BiometricHelper by inject()

    val biometricConfig = BiometricConfig.strong(
        title    = "Activer la biométrie",
        subtitle = "Confirmez votre identité pour activer la connexion biométrique",
    )

    init {
        updateState {
            copy(
                user               = session.currentUser(),
                biometricAvailable = biometric.canAuthenticate(biometricConfig),
                biometricEnabled   = session.isBiometricEnabled(),
            )
        }
    }

    /**
     * Called when the user toggles the biometric switch on the profile page.
     *
     * - Enabling  → fires [ProfileContracts.UiEvent.ConfirmBiometricEnable] so the
     *               UI can show the biometric prompt. Only persists after confirmation.
     * - Disabling → immediately clears the flag + stored credentials.
     */
    fun onBiometricToggle(enabled: Boolean) {
        if (enabled) {
            if (!biometric.canAuthenticate(biometricConfig)) {
                updateState { copy(biometricStatusMessage = "La biométrie n'est pas disponible sur cet appareil.") }
                return
            }
            // Credentials are saved on every successful password login or register.
            // If missing, the user somehow has a session without ever entering a password
            // (edge case: manual data wipe). Ask them to re-authenticate.
            if (!session.hasBiometricCredentials()) {
                updateState {
                    copy(biometricStatusMessage = "Déconnectez-vous puis reconnectez-vous avec votre mot de passe pour activer la biométrie.")
                }
                return
            }
            // Ask the user to confirm with biometrics before enabling
            sendEvent(ProfileContracts.UiEvent.ConfirmBiometricEnable)
        } else {
            session.setBiometricEnabled(false)
            updateState { copy(biometricEnabled = false, biometricStatusMessage = "Connexion biométrique désactivée.") }
        }
    }

    /** Called after the confirmation biometric prompt succeeds. */
    fun onBiometricEnableConfirmed() {
        session.setBiometricEnabled(true)
        updateState { copy(biometricEnabled = true, biometricStatusMessage = "Connexion biométrique activée !") }
    }

    /** Called if the biometric confirmation prompt fails or is cancelled. */
    fun onBiometricEnableCancelled() {
        updateState { copy(biometricEnabled = false, biometricStatusMessage = "Activation annulée.") }
    }

    fun dismissStatusMessage() = updateState { copy(biometricStatusMessage = null) }

    fun logout() {
        session.clearSession()
        sendEvent(ProfileContracts.UiEvent.LoggedOut)
    }
}