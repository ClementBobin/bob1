package org.bob1.app.ui.screens.register

import android.app.Application
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import org.bob1.app.domain.model.Registration
import org.bob1.app.domain.usecase.RegisterUseCase
import org.bob1.app.ui.core.Bob1ViewModel

const val INVITATION_CODE_LENGTH = 8   // the API generates 8-character codes

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val hasCode: Boolean = false,      // false: create a new family, true: join one with a code
    val familyName: String = "",
    val code: String = "",
    val loading: Boolean = false,
) {
    val emailValid get() = email.trim().contains('@')
    val passwordValid get() = password.length >= 8
    val codeValid get() = !hasCode || code.trim().length == INVITATION_CODE_LENGTH
    val canSubmit get() = firstName.isNotBlank() && lastName.isNotBlank() && emailValid && passwordValid && codeValid && !loading

    /** First thing the person still has to fix; shown under the form once they started typing. */
    val hint: String? get() = when {
        email.isNotBlank() && !emailValid -> "Adresse e-mail invalide."
        password.isNotEmpty() && !passwordValid -> "Mot de passe : 8 caractères minimum."
        hasCode && code.isNotBlank() && !codeValid -> "Le code compte $INVITATION_CODE_LENGTH caractères."
        else -> null
    }
}

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
