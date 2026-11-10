package com.mirage.bob1.domain.model

data class Venue(
    val id: String,
    val name: String,
    val address: String?,
    val city: String?,
    val postalCode: String?,
) {
    /** Single line usable as a maps query. */
    val fullAddress: String get() = listOfNotNull(name, address, listOfNotNull(postalCode, city).joinToString(" ").ifBlank { null }).joinToString(", ")
}
