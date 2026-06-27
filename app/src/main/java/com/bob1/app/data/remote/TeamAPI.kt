package com.bob1.app.data.remote

import com.bob1.app.data.dto.TeamDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Passerelle vers les endpoints d'équipes de l'API (/api/teams)
 */
internal class TeamAPI(private val client: HttpClient) {

    suspend fun getTeams(divisionId: String? = null): List<TeamDto> =
        client.get("/api/teams") {
            divisionId?.let { parameter("divisionId", it) }
        }.body()

    suspend fun createTeam(name: String, divisionId: String): TeamDto =
        client.post("/api/teams") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "divisionId" to divisionId))
        }.body()

    suspend fun updateTeam(id: String, name: String): TeamDto =
        client.put("/api/teams/$id") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name))
        }.body()

    suspend fun deleteTeam(id: String): HttpResponse =
        client.delete("/api/teams/$id")
}