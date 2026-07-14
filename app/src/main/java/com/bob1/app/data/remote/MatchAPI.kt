package com.bob1.app.data.remote

import com.bob1.app.data.dto.MatchDto
import com.bob1.app.data.dto.OfficialRole
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class MatchAPI(private val client: HttpClient) {

    suspend fun getMatches(year: Int, month: Int): List<MatchDto> =
        client.get("/api/matches/by-month") {
            parameter("year", year)
            parameter("month", month)
        }.body()

    suspend fun getMatchesByDivision(divisionId: String): List<MatchDto> =
        client.get("/api/matches/by-division/$divisionId").body()

    suspend fun getMatch(matchId: String): MatchDto =
        client.get("/api/matches/$matchId").body()

    suspend fun subscribeToMatch(matchId: String, role: OfficialRole): MatchDto =
        client.post("/api/matches/$matchId/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("role" to role.toApiString()))
        }.body()

    suspend fun unsubscribeFromMatch(matchId: String): MatchDto =
        client.post("/api/matches/$matchId/unsubscribe").body()

    suspend fun confirmPresence(matchId: String): MatchDto =
        client.post("/api/matches/$matchId/confirm").body()
}
