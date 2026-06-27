package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.*
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// ── In-memory mutable state (simulates server state per session) ──────────────

private val mutableMatches = BasketballMockData.matches.toMutableList()
private val mutableNotifications = BasketballMockData.notifications.toMutableList()
private var mutableSettings = BasketballMockData.settings

private val json = Json { ignoreUnknownKeys = true }

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

// ── Divisions ─────────────────────────────────────────────────────────────────

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

// ── Teams ─────────────────────────────────────────────────────────────────────

val teamHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/teams") { params, _ ->
        val divId = params["divisionId"]
        if (divId != null) BasketballMockData.teams.filter { it.divisionId == divId }
        else BasketballMockData.teams
    },

    MockHandler(HttpMethod.Post, "/teams") { _, body ->
        val obj    = body?.let { json.parseToJsonElement(it).jsonObject }
        val name   = obj?.get("name")?.jsonPrimitive?.content ?: error("name required")
        val divId  = obj?.get("divisionId")?.jsonPrimitive?.content ?: error("divisionId required")
        TeamDto(id = "t-new-${name.hashCode()}", name = name, divisionId = divId)
    },

    MockHandler(HttpMethod.Delete, "/teams/:id") { params, _ ->
        MessageResponseDto("Équipe ${params["id"]} supprimée.")
    },
)

// ── Matches ───────────────────────────────────────────────────────────────────

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
        val awayId   = obj?.get("awayTeamId")?.jsonPrimitive?.content ?: error("awayTeamId required")
        val divId    = obj?.get("divisionId")?.jsonPrimitive?.content ?: error("divisionId required")
        val dateIso  = obj?.get("dateIso")?.jsonPrimitive?.content ?: error("dateIso required")
        val location = obj?.get("location")?.jsonPrimitive?.content ?: ""
        val home = BasketballMockData.teams.firstOrNull { it.id == homeId }
            ?: TeamDto(homeId, "Équipe $homeId", divId)
        val away = BasketballMockData.teams.firstOrNull { it.id == awayId }
            ?: TeamDto(awayId, "Équipe $awayId", divId)
        val div  = BasketballMockData.divisions.firstOrNull { it.id == divId }
        val arbitres = obj?.get("arbitreSlots")?.jsonPrimitive?.content?.toIntOrNull() ?: 2
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

// ── Notifications ─────────────────────────────────────────────────────────────

val notificationHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/notifications") { _, _ ->
        mutableNotifications.sortedBy { it.isRead }
    },

    MockHandler(HttpMethod.Get, "/notifications/unread-count") { _, _ ->
        mapOf("count" to mutableNotifications.count { !it.isRead })
    },

    MockHandler(HttpMethod.Post, "/notifications/:id/read") { params, _ ->
        val id  = params["id"] ?: error("id required")
        val idx = mutableNotifications.indexOfFirst { it.id == id }
        if (idx >= 0) mutableNotifications[idx] = mutableNotifications[idx].copy(isRead = true)
        MessageResponseDto("Notification marquée comme lue.")
    },
)

// ── Admin: Settings ───────────────────────────────────────────────────────────

val settingsHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/admin/settings") { _, _ ->
        mutableSettings
    },

    MockHandler(HttpMethod.Put, "/admin/settings") { _, body ->
        val obj = body?.let { json.parseToJsonElement(it).jsonObject }
        val j15 = obj?.get("confirmationOffsetJ15")?.jsonPrimitive?.content?.toIntOrNull()
            ?: mutableSettings.confirmationOffsetJ15
        val j4  = obj?.get("confirmationOffsetJ4")?.jsonPrimitive?.content?.toIntOrNull()
            ?: mutableSettings.confirmationOffsetJ4
        mutableSettings = AppSettingsDto(j15, j4)
        mutableSettings
    },
)

// ── Admin: Point rules ────────────────────────────────────────────────────────

val pointRuleHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/admin/point-rules") { _, _ ->
        BasketballMockData.pointRules
    },

    MockHandler(HttpMethod.Put, "/admin/point-rules/:id") { params, body ->
        val id = params["id"] ?: error("id required")
        BasketballMockData.pointRules.firstOrNull { it.id == id }
            ?: error("Règle $id non trouvée.")
    },
)