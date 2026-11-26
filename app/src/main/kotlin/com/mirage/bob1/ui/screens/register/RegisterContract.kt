package com.mirage.bob1.ui.screens.register

import com.mirage.bob1.domain.model.PasswordPolicy
import com.mirage.bob1.domain.model.PasswordRule

const val INVITATION_CODE_LENGTH = 8   // the API generates 8-character codes

/** What the person does about a family while signing up. Everything can also be done later from the profile. */
enum class FamilyMode { Create, Join, Skip }

data class RegisterState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val isParent: Boolean = true,      // false: a staff member (official, table crew...), who cannot create a family
    val familyMode: FamilyMode = FamilyMode.Create,
    val familyName: String = "",
    val code: String = "",
    val loading: Boolean = false,
) {
    val emailValid get() = email.trim().contains('@')
    val passwordRules: List<PasswordRule> get() = PasswordPolicy.rules(password, firstName, lastName, email)
    val passwordValid get() = passwordRules.all { it.ok }
    val codeValid get() = familyMode != FamilyMode.Join || code.trim().length == INVITATION_CODE_LENGTH
    val canSubmit get() = firstName.isNotBlank() && lastName.isNotBlank() && emailValid && passwordValid && codeValid && !loading

    /** The choices offered for the family: only parents can start one. */
    val familyModes: List<FamilyMode> get() =
        if (isParent) listOf(FamilyMode.Create, FamilyMode.Join, FamilyMode.Skip) else listOf(FamilyMode.Join, FamilyMode.Skip)

    /** First thing the person still has to fix (besides the password rules, shown as a checklist); shown once they started typing. */
    val hint: String? get() = when {
        email.isNotBlank() && !emailValid -> "Adresse e-mail invalide."
        familyMode == FamilyMode.Join && code.isNotBlank() && !codeValid -> "Le code compte $INVITATION_CODE_LENGTH caractères."
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
    val setParent: (Boolean) -> Unit,
    val setFamilyMode: (FamilyMode) -> Unit,
    val submit: () -> Unit,
    val back: () -> Unit,
)
