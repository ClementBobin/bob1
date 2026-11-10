package org.bob1.app.domain.repository

import java.time.Instant
import org.bob1.app.domain.model.*

interface CarpoolRepository {
    suspend fun rides(matchId: String): Result<List<Ride>>
    suspend fun offerRide(matchId: String, seats: Int, departure: Instant, notes: String?): Result<Unit>
    suspend fun cancelRide(rideId: String): Result<Unit>
    suspend fun book(rideId: String): Result<Unit>
    suspend fun cancelBooking(rideId: String): Result<Unit>
}
