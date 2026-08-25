package com.mirage.bob1.mock.handlers

import com.mirage.bob1.mock.factories.BasketballMockData
import com.mirage.bob1.mock.registry.MockHandler
import io.ktor.http.HttpMethod

val pointRuleHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/admin/point-rules") { _, _ ->
        BasketballMockData.pointRules
    },

    MockHandler(HttpMethod.Put, "/admin/point-rules/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        BasketballMockData.pointRules.firstOrNull { it.id == id }
            ?: error("Règle $id non trouvée.")
    },
)