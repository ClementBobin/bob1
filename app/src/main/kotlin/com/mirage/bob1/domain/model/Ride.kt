package com.mirage.bob1.domain.model

import java.time.Instant

data class Ride(
    val id: String,
    val driverName: String,
    val seats: Int,
    val seatsLeft: Int,
    val departure: Instant,
    val notes: String?,
    val isMine: Boolean,
    val bookedByMe: Boolean,
)
