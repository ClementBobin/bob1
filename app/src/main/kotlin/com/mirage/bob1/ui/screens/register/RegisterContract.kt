package com.mirage.bob1.ui.screens.register

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

/** What [RegisterContent] can ask for; keeps the content stateless and previewable. */
data class RegisterActions(
    val onCode: (String) -> Unit,
    val onEmail: (String) -> Unit,
    val onFamilyName: (String) -> Unit,
    val onFirstName: (String) -> Unit,
    val onLastName: (String) -> Unit,
    val onPassword: (String) -> Unit,
    val setHasCode: (Boolean) -> Unit,
    val submit: () -> Unit,
    val back: () -> Unit,
)
