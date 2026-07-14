package com.bob1.app.mock.handlers

import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod

val locationHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/locations") { _, _ ->
        BasketballMockData.locations
    },

    MockHandler(HttpMethod.Get, "/locations/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        BasketballMockData.locations.firstOrNull { it.id == id }
            ?: error("Location non trouvée.")
    },
)
