package com.bob1.app.data.remote

import com.bob1.app.data.dto.DivisionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * Passerelle vers les endpoints de divisions de l'API (/api/divisions)
 */
internal class DivisionAPI(private val client: HttpClient) {

    suspend fun getDivisions(): List<DivisionDto> =
        client.get("/api/divisions").body()
}