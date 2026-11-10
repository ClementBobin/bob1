package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.CarpoolAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Ride
import com.mirage.bob1.domain.repository.CarpoolRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CarpoolRepositoryImpl @Inject constructor(
    private val carpoolAPI: CarpoolAPI,
) : CarpoolRepository {

    override suspend fun rides(matchId: String): Result<List<Ride>> =
        apiCall { carpoolAPI.getRides(matchId).map { it.toDomain() } }

    override suspend fun offerRide(matchId: String, seats: Int, departure: Instant, notes: String?): Result<Unit> =
        apiCall { carpoolAPI.offerRide(matchId, seats, departure.toString(), notes) }

    override suspend fun cancelRide(rideId: String): Result<Unit> = apiCall { carpoolAPI.cancelRide(rideId) }
    override suspend fun book(rideId: String): Result<Unit> = apiCall { carpoolAPI.book(rideId) }
    override suspend fun cancelBooking(rideId: String): Result<Unit> = apiCall { carpoolAPI.cancelBooking(rideId) }
}
