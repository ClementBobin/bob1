package org.bob1.app.domain.model

/**
 * Sign-up form. With [invitationCode] the person joins an existing family; without it a new family is created
 * (named [familyName], or the last name when blank).
 */
data class Registration(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val familyName: String? = null,
    val invitationCode: String? = null,
)
