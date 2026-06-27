package com.bob1.app.mock.handlers

import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
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