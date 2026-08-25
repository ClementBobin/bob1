package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.LocationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class LocationAPI(private val client: HttpClient) {

    suspend fun getLocations(): List<LocationDto> =
        client.get("/api/locations").body()
}
