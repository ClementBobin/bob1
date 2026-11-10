package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OfferRideRequestDto(
    val seats: Int,
    val departureUtc: String,
    val notes: String? = null,
)
