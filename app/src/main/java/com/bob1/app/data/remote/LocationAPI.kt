package com.bob1.app.data.remote

import com.bob1.app.data.dto.LocationDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class LocationAPI(private val client: HttpClient) {

    suspend fun getLocations(): List<LocationDto> =
        client.get("/api/locations").body()
}
