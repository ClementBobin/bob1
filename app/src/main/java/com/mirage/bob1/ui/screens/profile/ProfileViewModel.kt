package com.mirage.bob1.ui.screens.profile

import android.app.Application
import com.mirage.bob1.data.local.SessionManager
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.repository.AuthRepository
import dev.kindling.android.natif.BiometricConfig
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object ProfileContracts {
    data class UiState(
        val user: User? = null,
        val biometricAvailable: Boolean = false,
        val biometricEnabled: Boolean = false,
        val isLoading: Boolean = false,
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

    private val repo: AuthRepository       by inject()
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
                biometricEnabled   = session.isBiometricEnabled() && session.hasBiometricToken(),
            )
        }
    }

    /**
     * Called when the user toggles the biometric switch on the profile page.
     *
     * - Enabling  → fires [ProfileContracts.UiEvent.ConfirmBiometricEnable] so the
     *               UI can show the biometric prompt. The server token is generated
     *               and stored only after the prompt succeeds.
     * - Disabling → revokes the server token then clears the local flag.
     */
    fun onBiometricToggle(enabled: Boolean) {
        if (enabled) {
            if (!biometric.canAuthenticate(biometricConfig)) {
                updateState { copy(biometricStatusMessage = "La biométrie n'est pas disponible sur cet appareil.") }
                return
            }
            // Ask the user to confirm with biometrics before calling the server
            sendEvent(ProfileContracts.UiEvent.ConfirmBiometricEnable)
        } else {
            disableBiometric()
        }
    }

    /**
     * Called after the confirmation biometric prompt succeeds.
     * Calls the server to generate a long-lived biometric token, then stores it
     * encrypted on-device and enables the biometric login flag.
     */
    fun onBiometricEnableConfirmed() {
        updateState { copy(isLoading = true, biometricStatusMessage = null) }
        fetchData(
            source   = { repo.generateAndSaveBiometricToken() },
            onResult = {
                onSuccess {
                    session.setBiometricEnabled(true)
                    updateState {
                        copy(
                            isLoading              = false,
                            biometricEnabled       = true,
                            biometricStatusMessage = "Connexion biométrique activée !",
                        )
                    }
                }
                onFailure { e ->
                    updateState {
                        copy(
                            isLoading              = false,
                            biometricEnabled       = false,
                            biometricStatusMessage = "Échec de l'activation : ${e.message}",
                        )
                    }
                }
            }
        )
    }

    /** Called if the biometric confirmation prompt fails or is cancelled. */
    fun onBiometricEnableCancelled() {
        updateState { copy(biometricEnabled = false, biometricStatusMessage = "Activation annulée.") }
    }

    fun dismissStatusMessage() = updateState { copy(biometricStatusMessage = null) }

    /**
     * Revokes the server-side biometric token then clears the local copy + flag.
     */
    private fun disableBiometric() {
        updateState { copy(isLoading = true) }
        fetchData(
            source   = { repo.removeBiometricToken() },
            onResult = {
                onSuccess {
                    updateState {
                        copy(
                            isLoading              = false,
                            biometricEnabled       = false,
                            biometricStatusMessage = "Connexion biométrique désactivée.",
                        )
                    }
                }
                onFailure { e ->
                    // Still disable locally even if the server call failed
                    session.setBiometricEnabled(false)
                    session.clearBiometricToken()
                    updateState {
                        copy(
                            isLoading              = false,
                            biometricEnabled       = false,
                            biometricStatusMessage = "Désactivée localement (erreur serveur : ${e.message}).",
                        )
                    }
                }
            }
        )
    }

    fun logout() {
        fetchData(
            source   = { repo.logout() },
            onResult = {
                onSuccess  { sendEvent(ProfileContracts.UiEvent.LoggedOut) }
                // Always navigate away even on network failure — session is cleared
                // inside AuthRepositoryImpl.logout() before the API call
                onFailure  { sendEvent(ProfileContracts.UiEvent.LoggedOut) }
            }
        )
    }
}