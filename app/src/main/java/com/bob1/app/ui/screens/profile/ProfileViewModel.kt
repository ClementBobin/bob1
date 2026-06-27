package com.bob1.app.ui.screens.profile

import android.app.Application
import com.bob1.app.data.local.SessionManager
import com.bob1.app.domain.model.User
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object ProfileContracts {
    data class UiState(
        val user: User? = null,
    )

    sealed interface UiEvent {
        object LoggedOut : UiEvent
    }
}

class ProfileViewModel(application: Application) :
    KViewModel<ProfileContracts.UiState>(ProfileContracts.UiState(), application) {

    private val session: SessionManager by inject()

    init { updateState { copy(user = session.currentUser()) } }

    fun logout() {
        session.clearSession()
        sendEvent(ProfileContracts.UiEvent.LoggedOut)
    }
}