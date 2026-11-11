package com.mirage.bob1.ui.screens.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.usecase.CreateInvitationUseCase
import com.mirage.bob1.domain.usecase.GetFamilyUseCase
import com.mirage.bob1.domain.usecase.GetMeUseCase
import com.mirage.bob1.domain.usecase.LogoutUseCase
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
) : Bob1ViewModel<ProfileState>(ProfileState(), application) {

    init {
        viewModelScope.launch { modeStore.mode.collect { m -> updateState { copy(mode = m) } } }
        viewModelScope.launch { permission.granted.collect { g -> updateState { copy(notificationsEnabled = g) } } }
        call({ getMe() }) { u -> updateState { copy(user = u) } }
        call({ getFamily() }, onError = {}) { f -> updateState { copy(family = f) } }
    }

    fun enableNotifications() = permission.request()
    fun setMode(mode: AppMode) = modeStore.set(mode)

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
