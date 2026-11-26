package com.mirage.bob1.domain.model

/**
 * Sign-up form. [isParent] false registers a staff member (official, table crew...). The family is optional: with [invitationCode]
 * the person joins an existing one; with [createFamily] (parents only, named [familyName] or the last name when blank) a new one
 * is created; with neither they start without a family and can create or join one later from their profile.
 */
data class Registration(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val isParent: Boolean = true,
    val familyName: String? = null,
    val invitationCode: String? = null,
    val createFamily: Boolean = true,
)
