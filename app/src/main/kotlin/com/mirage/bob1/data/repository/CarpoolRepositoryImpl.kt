package com.mirage.bob1.data.repository

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.*

@Singleton
class CarpoolRepositoryImpl @Inject constructor(private val api: Bob1Api) : CarpoolRepository {
    override suspend fun rides(matchId: String) = apiCall { api.rides(matchId, EmptyQueryDto()).map { it.toDomain() } }
    override suspend fun offerRide(matchId: String, seats: Int, departure: Instant, notes: String?) =
        apiCall { api.offerRide(matchId, OfferRideRequestDto(seats, departure.toString(), notes)) }
    override suspend fun cancelRide(rideId: String) = apiCall { api.cancelRide(rideId) }
    override suspend fun book(rideId: String) = apiCall { api.book(rideId) }
    override suspend fun cancelBooking(rideId: String) = apiCall { api.cancelBooking(rideId) }
}
