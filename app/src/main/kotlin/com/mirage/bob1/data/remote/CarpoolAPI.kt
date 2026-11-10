package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.EmptyQueryDto
import com.mirage.bob1.data.dto.OfferRideRequestDto
import com.mirage.bob1.data.dto.RideDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.post
import javax.inject.Inject

internal class CarpoolAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /matches/{id}/rides */
    suspend fun getRides(matchId: String): List<RideDto> =
        client.query("matches/$matchId/rides", EmptyQueryDto()).body()

    suspend fun offerRide(matchId: String, seats: Int, departureUtc: String, notes: String?) {
        client.post("matches/$matchId/rides") { setBodyJson(OfferRideRequestDto(seats, departureUtc, notes)) }.ok()
    }

    suspend fun cancelRide(rideId: String) {
        client.delete("rides/$rideId").ok()
    }

    suspend fun book(rideId: String) {
        client.post("rides/$rideId/booking").ok()
    }

    suspend fun cancelBooking(rideId: String) {
        client.delete("rides/$rideId/booking").ok()
    }
}
