package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.MessageResponseDto
import com.bob1.app.data.dto.TeamDto
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val teamHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/teams") { params, _ ->
        val divId = params["divisionId"]
        if (divId != null) BasketballMockData.teams.filter { it.divisionId == divId }
        else BasketballMockData.teams
    },

    MockHandler(HttpMethod.Post, "/teams") { _, body ->
        val obj    = body?.let { json.parseToJsonElement(it).jsonObject }
        val name   = obj?.get("name")?.jsonPrimitive?.content ?: error("name required")
        val divId  = obj["divisionId"]?.jsonPrimitive?.content ?: error("divisionId required")
        TeamDto(id = "t-new-${name.hashCode()}", name = name, divisionId = divId)
    },

    MockHandler(HttpMethod.Delete, "/teams/:id") { params, _ ->
        MessageResponseDto("Équipe ${params["id"]} supprimée.")
    },
)