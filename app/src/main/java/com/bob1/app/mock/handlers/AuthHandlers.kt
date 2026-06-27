package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.*
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val json = Json { ignoreUnknownKeys = true }

// ── Auth ──────────────────────────────────────────────────────────────────────

val authHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Post, "/auth/login") { _, body ->
        val obj   = body?.let { json.parseToJsonElement(it).jsonObject }
        val email = obj?.get("email")?.jsonPrimitive?.content ?: ""
        when (email) {
            "admin@club.fr"   -> LoginResponseDto("mock-token-admin",   BasketballMockData.adminUser)
            "arbitre@club.fr" -> LoginResponseDto("mock-token-official", BasketballMockData.officialUser)
            else -> error("Email ou mot de passe incorrect.")
        }
    },

    MockHandler(HttpMethod.Post, "/auth/logout") { _, _ ->
        MessageResponseDto("Déconnecté.")
    },

    MockHandler(HttpMethod.Get, "/auth/me") { _, _ ->
        BasketballMockData.officialUser
    },
)