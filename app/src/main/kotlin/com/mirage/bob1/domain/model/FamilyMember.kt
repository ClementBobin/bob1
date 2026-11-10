package com.mirage.bob1.domain.model

data class FamilyMember(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isMe: Boolean,
    val divisionId: String?,
    val divisionName: String?,
) {
    val fullName get() = "$firstName $lastName".trim()
    val initials get() = "${firstName.firstOrNull() ?: ' '}${lastName.firstOrNull() ?: ' '}".trim().uppercase()
    /** Children have a division, adults don't. */
    val isChild get() = divisionId != null
}
