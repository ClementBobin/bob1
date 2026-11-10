package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

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
