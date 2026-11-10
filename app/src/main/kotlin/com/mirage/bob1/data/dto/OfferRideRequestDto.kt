package com.mirage.bob1ob1ob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OfferRideRequestDto(
    val seats: Int,
    val departureUtc: String,
    val notes: String? = null,
)
