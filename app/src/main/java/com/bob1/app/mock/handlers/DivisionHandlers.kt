package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.DivisionDto
import com.bob1.app.data.dto.MessageResponseDto
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val divisionHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/divisions") { _, _ ->
        BasketballMockData.divisions
    },

    MockHandler(HttpMethod.Post, "/divisions") { _, body ->
        val obj  = body?.let { json.parseToJsonElement(it).jsonObject }
        val name = obj?.get("name")?.jsonPrimitive?.content ?: error("name required")
        DivisionDto(id = "div-${name.lowercase()}", name = name)
    },

    MockHandler(HttpMethod.Delete, "/divisions/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        MessageResponseDto("Division $id supprimée.")
    },
)