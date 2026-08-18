package com.bob1.app.mock.handlers

import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val mutableMatches = BasketballMockData.matches.toMutableList()

val matchHandlers: List<MockHandler> = listOf(

    // GET /matches/by-month?year=&month=
    MockHandler(HttpMethod.Get, "/matches/by-month") { params, _ ->
        val year  = params["year"]?.toIntOrNull()
        val month = params["month"]?.toIntOrNull()
        mutableMatches.filter { m ->
            (year  == null || m.dateUtc.startsWith("$year-")) &&
            (month == null || m.dateUtc.substring(5, 7).toIntOrNull() == month)
        }
    },

    // GET /matches/by-division/:divisionId
    MockHandler(HttpMethod.Get, "/matches/by-division/:divisionId") { params, _ ->
        val divId = params["divisionId"] ?: error("divisionId required")
        mutableMatches.filter { it.division.id == divId }
    },

    // GET /matches/:id
    MockHandler(HttpMethod.Get, "/matches/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        mutableMatches.firstOrNull { it.id == id } ?: error("Match non trouvé.")
    },

    // POST /matches/:id/subscribe
    MockHandler(HttpMethod.Post, "/matches/:id/subscribe") { params, body ->
        val id   = params["id"] ?: error("id required")
        val obj  = body?.let { json.parseToJsonElement(it).jsonObject }
        val role = obj?.get("role")?.jsonPrimitive?.content ?: error("role required")
        val idx  = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 }
            ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        val updatedSlots = match.slots.map { slot ->
            if (slot.role == role && slot.assignedUser == null)
                slot.copy(assignedUser = BasketballMockData.officialUser)
            else slot
        }
        val updated = match.copy(
            slots             = updatedSlots,
            currentUserStatus = 1, // Subscribed
        )
        mutableMatches[idx] = updated
        updated
    },

    // POST /matches/:id/unsubscribe
    MockHandler(HttpMethod.Post, "/matches/:id/unsubscribe") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 }
            ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        val updatedSlots = match.slots.map { slot ->
            if (slot.assignedUser?.id == "u-official") slot.copy(assignedUser = null)
            else slot
        }
        val updated = match.copy(
            slots             = updatedSlots,
            currentUserStatus = 0, // Neutral
        )
        mutableMatches[idx] = updated
        updated
    },

    // POST /matches/:id/confirm
    MockHandler(HttpMethod.Post, "/matches/:id/confirm") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 }
            ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        // 2 = ConfirmedJ15, 3 = ConfirmedJ4
        val newStatus = if (match.currentUserStatus == 2) 3 else 2
        val updated = match.copy(currentUserStatus = newStatus)
        mutableMatches[idx] = updated
        updated
    },
)