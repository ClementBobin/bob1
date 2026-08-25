package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.PenaltyDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse

internal class PenaltyAPI(private val client: HttpClient) {

    suspend fun getMyPenalties(): List<PenaltyDto> =
        client.get("/api/penalties").body()

    suspend fun acknowledgePenalty(penaltyId: String): HttpResponse =
        client.post("/api/penalties/$penaltyId/acknowledge")
}
