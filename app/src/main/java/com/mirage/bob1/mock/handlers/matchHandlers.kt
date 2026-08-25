package com.mirage.bob1ob1.mock.handlers

import com.mirage.bob1ob1.data.dto.MinMatchDto
import com.mirage.bob1ob1.mock.factories.BasketballMockData
import com.mirage.bob1ob1.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val mutableMatches = BasketballMockData.matches.toMutableList()

val matchHandlers: List<MockHandler> = listOf(

    // GET /matches/by-month?year=&month= — returns MinMatchDto (no homeTeam/awayTeam/slots)
    MockHandler(HttpMethod.Get, "/matches/by-month") { params, _ ->
        val year  = params["year"]?.toIntOrNull()
        val month = params["month"]?.toIntOrNull()
        mutableMatches
            .filter { m ->
                (year  == null || m.dateUtc.startsWith("$year-")) &&
                (month == null || m.dateUtc.substring(5, 7).toIntOrNull() == month)
            }
            .map { m ->
                MinMatchDto(
                    id                = m.id,
                    dateUtc           = m.dateUtc,
                    division          = m.division,
                    location          = m.location,
                    areSlotsAvailable = m.slots.any { it.assignedUser == null },
                    currentUserStatus = m.currentUserStatus,
                )
            }
    },

    // GET /matches/by-division/:divisionId — returns full MatchDto
    MockHandler(HttpMethod.Get, "/matches/by-division/:divisionId") { params, _ ->
        val divId = params["divisionId"] ?: error("divisionId required")
        mutableMatches.filter { it.division.id == divId }
    },

    // GET /matches/:id — returns full MatchDto
    MockHandler(HttpMethod.Get, "/matches/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        mutableMatches.firstOrNull { it.id == id } ?: error("Match non trouvé.")
    },

    // POST /matches/:id/subscribe — role is now sent as integer (OfficialRole int)
    MockHandler(HttpMethod.Post, "/matches/:id/subscribe") { params, body ->
        val id     = params["id"] ?: error("id required")
        val obj    = body?.let { json.parseToJsonElement(it).jsonObject }
        val roleInt = obj?.get("role")?.jsonPrimitive?.int ?: error("role required")
        val idx    = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 }
            ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        val updatedSlots = match.slots.map { slot ->
            if (slot.role == roleInt && slot.assignedUser == null)
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