package com.bob1.app.data.remote

import com.bob1.app.data.dto.CreateMatchRequestDto
import com.bob1.app.data.dto.MatchDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Passerelle vers les endpoints de matchs de l'API (/api/matches)
 */
internal class MatchAPI(private val client: HttpClient) {

    suspend fun getMatches(
        divisionId: String? = null,
        year: Int,
        month: Int
    ): List<MatchDto> =
        client.get("/api/matches") {
            divisionId?.let { parameter("divisionId", it) }
            parameter("year", year)
            parameter("month", month)
        }.body()

    suspend fun getMatch(matchId: String): MatchDto =
        client.get("/api/matches/$matchId").body()

    suspend fun subscribeToMatch(matchId: String, role: String): MatchDto =
        client.post("/api/matches/$matchId/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("role" to role))
        }.body()

    suspend fun unsubscribeFromMatch(matchId: String): MatchDto =
        client.post("/api/matches/$matchId/unsubscribe").body()

    suspend fun confirmPresence(matchId: String): MatchDto =
        client.post("/api/matches/$matchId/confirm").body()

    suspend fun createMatch(request: CreateMatchRequestDto): MatchDto =
        client.post("/api/matches") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun updateMatch(matchId: String, params: Map<String, String>): MatchDto =
        client.put("/api/matches/$matchId") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun deleteMatch(matchId: String): HttpResponse =
        client.delete("/api/matches/$matchId")
}