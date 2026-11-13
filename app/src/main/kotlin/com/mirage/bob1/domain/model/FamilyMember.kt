package com.mirage.bob1.domain.model

data class FamilyMember(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isMe: Boolean,
    val divisionId: String?,
    val divisionName: String?,
    val teamIds: List<String> = emptyList(),
) {
    val fullName get() = "$firstName $lastName".trim()
    val initials get() = "${firstName.firstOrNull() ?: ' '}${lastName.firstOrNull() ?: ' '}".trim().uppercase()
    /** Children play (a division, teams); adults don't. */
    val isChild get() = divisionId != null || teamIds.isNotEmpty()
}
