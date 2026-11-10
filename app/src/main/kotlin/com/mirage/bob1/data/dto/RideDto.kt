package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class RideDto(
    val id: String,
    val driverName: String,
    val seats: Int,
    val seatsLeft: Int,
    val departureUtc: String,
    val notes: String? = null,
    val isMine: Boolean = false,
    val bookedByMe: Boolean = false,
) {
    fun toDomain() = Ride(id, driverName, seats, seatsLeft, parseUtc(departureUtc), notes, isMine, bookedByMe)
}
