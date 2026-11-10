package org.bob1.app.data.repository

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.dto.*
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.repository.*

@Singleton
class CarpoolRepositoryImpl @Inject constructor(private val api: Bob1Api) : CarpoolRepository {
    override suspend fun rides(matchId: String) = apiCall { api.rides(matchId, EmptyQueryDto()).map { it.toDomain() } }
    override suspend fun offerRide(matchId: String, seats: Int, departure: Instant, notes: String?) =
        apiCall { api.offerRide(matchId, OfferRideRequestDto(seats, departure.toString(), notes)) }
    override suspend fun cancelRide(rideId: String) = apiCall { api.cancelRide(rideId) }
    override suspend fun book(rideId: String) = apiCall { api.book(rideId) }
    override suspend fun cancelBooking(rideId: String) = apiCall { api.cancelBooking(rideId) }
}
