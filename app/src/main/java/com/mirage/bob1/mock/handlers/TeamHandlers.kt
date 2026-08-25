package com.mirage.bob1.mock.handlers

import com.mirage.bob1.data.dto.DivisionDto
import com.mirage.bob1.data.dto.MessageResponseDto
import com.mirage.bob1.data.dto.TeamDto
import com.mirage.bob1.mock.factories.BasketballMockData
import com.mirage.bob1.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val teamHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/teams") { params, _ ->
        val divId = params["divisionId"]
        if (divId != null) BasketballMockData.teams.filter { it.division.id == divId }
        else BasketballMockData.teams
    },

    MockHandler(HttpMethod.Get, "/teams/by-division/:divisionId") { params, _ ->
        val divId = params["divisionId"] ?: error("divisionId required")
        BasketballMockData.teams.filter { it.division.id == divId }
    },

    MockHandler(HttpMethod.Get, "/teams/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        BasketballMockData.teams.firstOrNull { it.id == id } ?: error("Équipe non trouvée.")
    },

    MockHandler(HttpMethod.Post, "/teams") { _, body ->
        val obj   = body?.let { json.parseToJsonElement(it).jsonObject }
        val name  = obj?.get("name")?.jsonPrimitive?.content ?: error("name required")
        val divId = obj["divisionId"]?.jsonPrimitive?.content ?: error("divisionId required")
        val div   = BasketballMockData.divisions.firstOrNull { it.id == divId }
            ?: DivisionDto(divId, divId)
        TeamDto(id = "t-new-${name.hashCode()}", name = name, logoUrl = null, division = div)
    },

    MockHandler(HttpMethod.Delete, "/teams/:id") { params, _ ->
        MessageResponseDto("Équipe ${params["id"]} supprimée.")
    },
)
