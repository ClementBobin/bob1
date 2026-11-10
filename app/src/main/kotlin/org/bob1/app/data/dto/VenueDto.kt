package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

@Serializable
data class VenueDto(
    val id: String,
    val name: String,
    val address: String? = null,
    val city: String? = null,
    val postalCode: String? = null,
) {
    fun toDomain() = Venue(id, name, address, city, postalCode)
}
