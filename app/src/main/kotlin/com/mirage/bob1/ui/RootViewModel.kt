package com.mirage.bob1.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.domain.usecase.GetMeUseCase
import com.mirage.bob1.domain.usecase.ObserveSessionUseCase
import com.mirage.bob1.domain.usecase.RegisterPushTokenUseCase
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.core.AppModeStore
import com.mirage.bob1.ui.core.Bob1ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.bob1.capacity.permission.NotificationPermission

@HiltViewModel
class RootViewModel @Inject constructor(
    application: Application,
    observeSession: ObserveSessionUseCase,
    private val registerPushToken: RegisterPushTokenUseCase,
    private val notificationPermission: NotificationPermission,
    private val getMe: GetMeUseCase,
    private val modeStore: AppModeStore,
) : Bob1ViewModel<RootState>(RootState(), application) {

    init {
        viewModelScope.launch { modeStore.mode.collect { m -> updateState { copy(mode = m) } } }
        collectData({ observeSession() }) {
            onSuccess { authed ->
                updateState { copy(authenticated = authed, roles = if (authed == true) roles else null) }
                if (authed == true) {
                    loadRoles()
                    call({ registerPushToken() }, onError = {})   // a fresh login or a restored session: make sure the API knows this device
                    notificationPermission.request()
                }
            }
        }
    }

    /** Roles decide which tabs exist. If the profile can't be loaded (offline), fall back to the parent experience. */
    private fun loadRoles() = call(
        block = { getMe() },
        onError = { updateState { copy(roles = setOf(UserRole.Parent)) } },
    ) { me ->
        val roles = me.roles.ifEmpty { setOf(UserRole.Parent) }
        modeStore.set(if (UserRole.Parent in roles) AppMode.Parent else AppMode.Staff)
        updateState { copy(roles = roles) }
    }
}
