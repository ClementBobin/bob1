package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.*
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val json = Json { ignoreUnknownKeys = true }

val authHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Post, "/auth/login") { _, body ->
        val obj   = body?.let { json.parseToJsonElement(it).jsonObject }
        val email = obj?.get("email")?.jsonPrimitive?.content ?: ""
        when (email) {
            "admin@club.fr"   -> LoginResponseDto(
                "mock-token-admin",
                expiresTime = TODO()
            )
            "arbitre@club.fr" -> LoginResponseDto(
                "mock-token-official",
                expiresTime = TODO()
            )
            else -> error("Email ou mot de passe incorrect.")
        }
    },

    MockHandler(HttpMethod.Get, "/auth/me") { headers, _ ->
        when (headers["Authorization"]) {
            "Bearer mock-token-admin"    -> BasketballMockData.adminUser
            "Bearer mock-token-official" -> BasketballMockData.officialUser
            else                         -> BasketballMockData.officialUser
        }
    },

    MockHandler(HttpMethod.Post, "/auth/register", HttpStatusCode.Created) { _, body ->
        val obj   = body?.let { json.parseToJsonElement(it).jsonObject }
        val email = obj?.get("email")?.jsonPrimitive?.content ?: error("email required")
        val first = obj["firstName"]?.jsonPrimitive?.content ?: ""
        val last  = obj["lastName"]?.jsonPrimitive?.content ?: ""
        UserDto(
            id        = "u-new-${System.currentTimeMillis()}",
            email     = email,
            firstName = first,
            lastName  = last,
            role      = 0, // Official
        )
    },

    MockHandler(HttpMethod.Post, "/auth/logout") { _, _ ->
        MessageResponseDto("Déconnecté.")
    },


)