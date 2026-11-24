package com.mirage.bob1.domain.model

/** What the API hands back when a device is enrolled: the one copy of the token, and the id used to revoke it later. */
data class BiometricEnrollment(val token: String, val credentialId: String)
