package com.bob1.app.mock.factories

import com.bob1.app.data.dto.*
import com.bob1.app.domain.model.OfficialRole
import java.text.SimpleDateFormat
import java.util.*

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun uuid() = UUID.randomUUID().toString()
private fun isoDate(year: Int, month: Int, day: Int, hour: Int = 15): String {
    val cal = Calendar.getInstance().apply {
        set(year, month - 1, day, hour, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        .also { it.timeZone = TimeZone.getTimeZone("UTC") }
        .format(cal.time)
}
private fun offsetDate(daysAgo: Int): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(cal.time)
}

// ── Static mock data ──────────────────────────────────────────────────────────

object BasketballMockData {

    // Divisions
    val divisions = listOf(
        DivisionDto(id = "div-u17", name = "U17"),
        DivisionDto(id = "div-u15", name = "U15"),
        DivisionDto(id = "div-u13", name = "U13"),
    )

    // Teams per division
    val teams = listOf(
        TeamDto(id = "t01", name = "Panthers Besançon",  divisionId = "div-u17"),
        TeamDto(id = "t02", name = "Lions Dijon",        divisionId = "div-u17"),
        TeamDto(id = "t03", name = "Eagles Belfort",     divisionId = "div-u17"),
        TeamDto(id = "t04", name = "Wolves Montbéliard", divisionId = "div-u17"),
        TeamDto(id = "t05", name = "Titans Besançon",    divisionId = "div-u15"),
        TeamDto(id = "t06", name = "Hawks Lons",         divisionId = "div-u15"),
        TeamDto(id = "t07", name = "Bears Pontarlier",   divisionId = "div-u15"),
        TeamDto(id = "t08", name = "Sharks Dole",        divisionId = "div-u15"),
        TeamDto(id = "t09", name = "Rockets Besançon",   divisionId = "div-u13"),
        TeamDto(id = "t10", name = "Comets Vesoul",      divisionId = "div-u13"),
        TeamDto(id = "t11", name = "Stars Gray",         divisionId = "div-u13"),
        TeamDto(id = "t12", name = "Jets Lure",          divisionId = "div-u13"),
    )

    // Users
    val officialUser = UserDto(
        id = "u-official", email = "arbitre@club.fr",
        firstName = "Marc", lastName = "Dupuis", role = "OFFICIAL"
    )
    val adminUser = UserDto(
        id = "u-admin", email = "admin@club.fr",
        firstName = "Sophie", lastName = "Laurent", role = "ADMIN"
    )

    // Matches — spread across current + next month
    val matches: List<MatchDto> by lazy { buildMatches() }

    private fun buildMatches(): List<MatchDto> {
        val now = Calendar.getInstance()
        val y = now.get(Calendar.YEAR)
        val m = now.get(Calendar.MONTH) + 1 // 1-based

        // next month
        val nm = if (m == 12) 1 else m + 1
        val ny = if (m == 12) y + 1 else y

        fun team(id: String) = teams.first { it.id == id }

        fun slots(arbitres: Int, chrono: Int = 1, mar: Int = 1): List<RoleSlotDto> {
            val s = mutableListOf<RoleSlotDto>()
            if (arbitres >= 1) s += RoleSlotDto("ARBITRE_1")
            if (arbitres >= 2) s += RoleSlotDto("ARBITRE_2", "u-official", "Marc Dupuis")
            if (arbitres >= 3) s += RoleSlotDto("ARBITRE_3")
            if (arbitres >= 4) s += RoleSlotDto("ARBITRE_4")
            repeat(chrono) { s += RoleSlotDto("CHRONO") }
            repeat(mar)    { s += RoleSlotDto("MAR") }
            return s
        }

        return listOf(
            // ── This month ────────────────────────────────────────────────────
            MatchDto(
                id = "m01", divisionId = "div-u17", divisionName = "U17",
                homeTeam = team("t01"), awayTeam = team("t02"),
                dateIso = isoDate(y, m, 5, 15),
                location = "Gymnase Pasteur, Besançon",
                slots = slots(2),
                subscriptionStatus = "CONFIRMED_J15", currentUserRole = "ARBITRE_2"
            ),
            MatchDto(
                id = "m02", divisionId = "div-u15", divisionName = "U15",
                homeTeam = team("t05"), awayTeam = team("t06"),
                dateIso = isoDate(y, m, 10, 14),
                location = "Salle Multiplex, Lons",
                slots = slots(2),
                subscriptionStatus = "NEUTRAL"
            ),
            MatchDto(
                id = "m03", divisionId = "div-u13", divisionName = "U13",
                homeTeam = team("t09"), awayTeam = team("t10"),
                dateIso = isoDate(y, m, 10, 16),
                location = "Gymnase Arènes, Besançon",
                slots = slots(2, chrono = 1, mar = 1),
                subscriptionStatus = "SUBSCRIBED", currentUserRole = "CHRONO"
            ),
            MatchDto(
                id = "m04", divisionId = "div-u17", divisionName = "U17",
                homeTeam = team("t03"), awayTeam = team("t04"),
                dateIso = isoDate(y, m, 17, 15),
                location = "Palais des Sports, Belfort",
                slots = slots(4),
                subscriptionStatus = "FULL"
            ),
            MatchDto(
                id = "m05", divisionId = "div-u15", divisionName = "U15",
                homeTeam = team("t07"), awayTeam = team("t08"),
                dateIso = isoDate(y, m, 22, 14),
                location = "Salle Pontarlier",
                slots = slots(2),
                subscriptionStatus = "CONFIRMED_J4", currentUserRole = "ARBITRE_1"
            ),
            MatchDto(
                id = "m06", divisionId = "div-u13", divisionName = "U13",
                homeTeam = team("t11"), awayTeam = team("t12"),
                dateIso = isoDate(y, m, 28, 10),
                location = "Gymnase Gray",
                slots = slots(2),
                subscriptionStatus = "NEUTRAL"
            ),
            // Two matches same day
            MatchDto(
                id = "m07", divisionId = "div-u17", divisionName = "U17",
                homeTeam = team("t02"), awayTeam = team("t03"),
                dateIso = isoDate(y, m, 22, 17),
                location = "Salle Dijon Nord",
                slots = slots(2),
                subscriptionStatus = "NEUTRAL"
            ),
            // ── Next month ────────────────────────────────────────────────────
            MatchDto(
                id = "m08", divisionId = "div-u17", divisionName = "U17",
                homeTeam = team("t01"), awayTeam = team("t04"),
                dateIso = isoDate(ny, nm, 8, 15),
                location = "Gymnase Pasteur, Besançon",
                slots = slots(2),
                subscriptionStatus = "NEUTRAL"
            ),
            MatchDto(
                id = "m09", divisionId = "div-u15", divisionName = "U15",
                homeTeam = team("t06"), awayTeam = team("t07"),
                dateIso = isoDate(ny, nm, 14, 14),
                location = "Salle Lons",
                slots = slots(2),
                emergencyDate = isoDate(ny, nm, 12, 0),
                emergencyPoints = 5,
                subscriptionStatus = "NEUTRAL"
            ),
            MatchDto(
                id = "m10", divisionId = "div-u13", divisionName = "U13",
                homeTeam = team("t09"), awayTeam = team("t12"),
                dateIso = isoDate(ny, nm, 20, 16),
                location = "Gymnase Arènes, Besançon",
                slots = slots(2),
                subscriptionStatus = "NEUTRAL"
            ),
        )
    }

    // Notifications
    val notifications = listOf(
        NotificationDto(
            id = "n1", type = "J15_REMINDER",
            title = "Confirmation J-15 requise",
            body  = "Confirmez votre présence pour U17 Panthers vs Lions le 8 ${currentMonthName()}",
            matchId = "m08",
            timestampIso = offsetDate(1),
            isRead = false
        ),
        NotificationDto(
            id = "n2", type = "J15_REMINDER",
            title = "Confirmation J-15 requise",
            body  = "Confirmez votre présence pour U15 Hawks vs Bears le 14 ${currentMonthName()}",
            matchId = "m09",
            timestampIso = offsetDate(2),
            isRead = false
        ),
        NotificationDto(
            id = "n3", type = "J4_REMINDER",
            title = "Confirmation finale J-4",
            body  = "Confirmation finale requise pour U15 Titans vs Hawks",
            matchId = "m05",
            timestampIso = offsetDate(4),
            isRead = false
        ),
        NotificationDto(
            id = "n4", type = "GENERAL",
            title = "Bienvenue sur BasketballRef",
            body  = "Votre compte arbitre est actif. Bonne saison !",
            matchId = null,
            timestampIso = offsetDate(30),
            isRead = true
        ),
        NotificationDto(
            id = "n5", type = "EMERGENCY",
            title = "Besoin urgent d'arbitres",
            body  = "Match U13 Rockets vs Comets le 10 — poste MAR non pourvu",
            matchId = "m03",
            timestampIso = offsetDate(5),
            isRead = true
        ),
    )

    val settings = AppSettingsDto(
        confirmationOffsetJ15 = 15,
        confirmationOffsetJ4  = 4,
    )

    val pointRules = OfficialRole.entries.mapIndexed { i, role ->
        PointRuleDto(
            id = "pr-$i",
            role = role.name,
            pointsOnJ15 = 10,
            pointsOnJ4  = 5,
            pointsEmergency = 15,
        )
    }

    private fun currentMonthName(): String =
        SimpleDateFormat("MMMM", Locale.FRENCH).format(Date())
}