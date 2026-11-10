package com.mirage.bob1.domain.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val roles: Set<UserRole>,
    val familyId: String?,
) {
    val fullName get() = "$firstName $lastName".trim()
    val initials get() = "${firstName.firstOrNull() ?: ' '}${lastName.firstOrNull() ?: ' '}".trim().uppercase()
}
