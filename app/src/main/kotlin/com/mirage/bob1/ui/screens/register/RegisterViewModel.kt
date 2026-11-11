package com.mirage.bob1.ui.screens.register

import android.app.Application
import com.mirage.bob1.domain.model.Registration
import com.mirage.bob1.domain.usecase.RegisterUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    application: Application,
    private val register: RegisterUseCase,
) : Bob1ViewModel<RegisterState>(RegisterState(), application) {

    fun onFirstName(v: String) = updateState { copy(firstName = v) }
    fun onLastName(v: String) = updateState { copy(lastName = v) }
    fun onEmail(v: String) = updateState { copy(email = v) }
    fun onPassword(v: String) = updateState { copy(password = v) }
    fun onFamilyName(v: String) = updateState { copy(familyName = v) }
    fun onCode(v: String) = updateState { copy(code = v.filter { it.isLetterOrDigit() }.uppercase().take(INVITATION_CODE_LENGTH)) }
    fun setHasCode(value: Boolean) = updateState { copy(hasCode = value) }

    fun submit() {
        val s = state.value
        if (!s.canSubmit) return
        updateState { copy(loading = true) }
        val form = Registration(
            firstName = s.firstName, lastName = s.lastName, email = s.email, password = s.password,
            familyName = if (s.hasCode) null else s.familyName,
            invitationCode = if (s.hasCode) s.code else null,
        )
        call(
            block = { register(form) },
            onError = { updateState { copy(loading = false) }; sendError(it) },
            // Success: the session is saved, so AppRoot leaves the auth graph on its own.
            onOk = { updateState { copy(loading = false) } },
        )
    }
}
