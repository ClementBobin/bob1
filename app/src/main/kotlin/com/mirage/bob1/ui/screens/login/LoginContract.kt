package com.mirage.bob1.ui.screens.login

data class LoginState(val email: String = "", val password: String = "", val loading: Boolean = false) {
    val canSubmit get() = email.isNotBlank() && password.isNotBlank() && !loading
}

/** What [LoginContent] can ask for; keeps the content stateless and previewable. */
data class LoginActions(
    val onEmail: (String) -> Unit,
    val onPassword: (String) -> Unit,
    val openRegister: () -> Unit,
    val submit: () -> Unit,
)
