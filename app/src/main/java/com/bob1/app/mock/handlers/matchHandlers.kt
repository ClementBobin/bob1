package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.MatchDto
import com.bob1.app.data.dto.MessageResponseDto
import com.bob1.app.data.dto.RoleSlotDto
import com.bob1.app.data.dto.TeamDto
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.text.toIntOrNull

private val mutableMatches = BasketballMockData.matches.toMutableList()

val matchHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/matches") { params, _ ->
        val divId = params["divisionId"]
        val year  = params["year"]?.toIntOrNull()
        val month = params["month"]?.toIntOrNull()
        mutableMatches.filter { m ->
            (divId == null || m.divisionId == divId) &&
                    (year == null  || m.dateIso.startsWith("$year-")) &&
                    (month == null || m.dateIso.substring(5, 7).toIntOrNull() == month)
        }
    },

    MockHandler(HttpMethod.Get, "/matches/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        mutableMatches.firstOrNull { it.id == id } ?: error("Match non trouvé.")
    },

    MockHandler(HttpMethod.Post, "/matches/:id/subscribe") { params, body ->
        val id   = params["id"] ?: error("id required")
        val obj  = body?.let { json.parseToJsonElement(it).jsonObject }
        val role = obj?.get("role")?.jsonPrimitive?.content ?: error("role required")
        val idx  = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        val updatedSlots = match.slots.map { slot ->
            if (slot.role == role && slot.assignedUserId == null)
                slot.copy(assignedUserId = "u-official", assignedUserName = "Marc Dupuis")
            else slot
        }
        val updated = match.copy(
            slots              = updatedSlots,
            subscriptionStatus = "SUBSCRIBED",
            currentUserRole    = role,
        )
        mutableMatches[idx] = updated
        updated
    },

    MockHandler(HttpMethod.Post, "/matches/:id/unsubscribe") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        val updatedSlots = match.slots.map { slot ->
            if (slot.assignedUserId == "u-official") slot.copy(assignedUserId = null, assignedUserName = null)
            else slot
        }
        val updated = match.copy(
            slots              = updatedSlots,
            subscriptionStatus = "NEUTRAL",
            currentUserRole    = null,
        )
        mutableMatches[idx] = updated
        updated
    },

    MockHandler(HttpMethod.Post, "/matches/:id/confirm") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableMatches.indexOfFirst { it.id == id }.takeIf { it >= 0 } ?: error("Match non trouvé.")
        val match = mutableMatches[idx]
        val newStatus = if (match.subscriptionStatus == "CONFIRMED_J15") "CONFIRMED_J4" else "CONFIRMED_J15"
        val updated = match.copy(subscriptionStatus = newStatus)
        mutableMatches[idx] = updated
        updated
    },

    // Admin: create match
    MockHandler(HttpMethod.Post, "/matches") { _, body ->
        val obj = body?.let { json.parseToJsonElement(it).jsonObject }
        val homeId   = obj?.get("homeTeamId")?.jsonPrimitive?.content ?: error("homeTeamId required")
        val awayId   = obj["awayTeamId"]?.jsonPrimitive?.content ?: error("awayTeamId required")
        val divId    = obj["divisionId"]?.jsonPrimitive?.content ?: error("divisionId required")
        val dateIso  = obj["dateIso"]?.jsonPrimitive?.content ?: error("dateIso required")
        val location = obj["location"]?.jsonPrimitive?.content ?: ""
        val home = BasketballMockData.teams.firstOrNull { it.id == homeId }
            ?: TeamDto(homeId, "Équipe $homeId", divId)
        val away = BasketballMockData.teams.firstOrNull { it.id == awayId }
            ?: TeamDto(awayId, "Équipe $awayId", divId)
        val div  = BasketballMockData.divisions.firstOrNull { it.id == divId }
        val arbitres = obj["arbitreSlots"]?.jsonPrimitive?.content?.toIntOrNull() ?: 2
        val slots = buildList {
            repeat(arbitres) { i -> add(RoleSlotDto("ARBITRE_${i+1}")) }
            add(RoleSlotDto("CHRONO"))
            add(RoleSlotDto("MAR"))
        }
        val newMatch = MatchDto(
            id = "m-new-${System.currentTimeMillis()}",
            divisionId = divId, divisionName = div?.name ?: divId,
            homeTeam = home, awayTeam = away,
            dateIso = dateIso, location = location,
            slots = slots,
        )
        mutableMatches.add(newMatch)
        newMatch
    },

    MockHandler(HttpMethod.Delete, "/matches/:id") { params, _ ->
        val id = params["id"] ?: error("id required")
        mutableMatches.removeAll { it.id == id }
        MessageResponseDto("Match $id supprimé.")
    },
)