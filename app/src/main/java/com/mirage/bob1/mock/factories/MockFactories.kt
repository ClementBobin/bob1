package com.mirage.bob1.mock.factories

import com.mirage.bob1.data.dto.*
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
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        .also { it.timeZone = TimeZone.getTimeZone("UTC") }
        .format(cal.time)
}

private fun currentMonthName(): String =
    SimpleDateFormat("MMMM", Locale.FRENCH).format(Date())

// ── Static mock data ──────────────────────────────────────────────────────────

object BasketballMockData {

    // Divisions
    val divisions = listOf(
        DivisionDto(id = "div-u17", name = "U17"),
        DivisionDto(id = "div-u15", name = "U15"),
        DivisionDto(id = "div-u13", name = "U13"),
    )

    // Teams — now use nested DivisionDto to match API shape
    val teams = listOf(
        TeamDto("t01", "Panthers Besançon",  null, DivisionDto("div-u17", "U17")),
        TeamDto("t02", "Lions Dijon",        null, DivisionDto("div-u17", "U17")),
        TeamDto("t03", "Eagles Belfort",     null, DivisionDto("div-u17", "U17")),
        TeamDto("t04", "Wolves Montbéliard", null, DivisionDto("div-u17", "U17")),
        TeamDto("t05", "Titans Besançon",    null, DivisionDto("div-u15", "U15")),
        TeamDto("t06", "Hawks Lons",         null, DivisionDto("div-u15", "U15")),
        TeamDto("t07", "Bears Pontarlier",   null, DivisionDto("div-u15", "U15")),
        TeamDto("t08", "Sharks Dole",        null, DivisionDto("div-u15", "U15")),
        TeamDto("t09", "Rockets Besançon",   null, DivisionDto("div-u13", "U13")),
        TeamDto("t10", "Comets Vesoul",      null, DivisionDto("div-u13", "U13")),
        TeamDto("t11", "Stars Gray",         null, DivisionDto("div-u13", "U13")),
        TeamDto("t12", "Jets Lure",          null, DivisionDto("div-u13", "U13")),
    )

    // Locations — now a proper LocationDto
    val locations = listOf(
        LocationDto("loc-01", "Gymnase Pasteur",   "Rue Pasteur, Besançon",          latitude = 47.2378, longitude = 6.0241),
        LocationDto("loc-02", "Salle Multiplex",   "Avenue des Sports, Lons",         latitude = 46.6741, longitude = 5.5541),
        LocationDto("loc-03", "Gymnase Arènes",    "Place des Arènes, Besançon",      latitude = 47.2480, longitude = 6.0170),
        LocationDto("loc-04", "Palais des Sports", "Boulevard Wilson, Belfort",       latitude = 47.6384, longitude = 6.8628),
        LocationDto("loc-05", "Salle Pontarlier",  "Rue de la Gare, Pontarlier"),
        LocationDto("loc-06", "Gymnase Gray",      "Allée du Stade, Gray"),
        LocationDto("loc-07", "Salle Dijon Nord",  "Avenue Jean Jaurès, Dijon",       latitude = 47.3220, longitude = 5.0415),
    )

    // Users — role now sent as integer: 0=Official, 1=Admin
    val officialUser = UserDto(
        id = "u-official", email = "arbitre@club.fr",
        firstName = "Marc", lastName = "Dupuis", role = 0
    )
    val adminUser = UserDto(
        id = "u-admin", email = "admin@club.fr",
        firstName = "Sophie", lastName = "Laurent", role = 1
    )

    // Matches — full MatchDto list (used by /matches/:id, /matches/by-division)
    val matches: List<MatchDto> by lazy { buildMatches() }

    // MinMatchDto list — used by /matches/by-month and /matches (list)
    val minMatches: List<MinMatchDto> by lazy {
        matches.map { m ->
            MinMatchDto(
                id                = m.id,
                dateUtc           = m.dateUtc,
                division          = m.division,
                location          = m.location,
                areSlotsAvailable = m.slots.any { it.assignedUser == null },
                currentUserStatus = m.currentUserStatus,
            )
        }
    }

    private fun buildMatches(): List<MatchDto> {
        val now = Calendar.getInstance()
        val y = now.get(Calendar.YEAR)
        val m = now.get(Calendar.MONTH) + 1
        val nm = if (m == 12) 1 else m + 1
        val ny = if (m == 12) y + 1 else y

        fun team(id: String) = teams.first { it.id == id }
        fun location(id: String) = locations.first { it.id == id }

        // RoleSlot now uses integer role (OfficialRole as int) and nested assignedUser: UserDto?
        fun slot(role: OfficialRole, assigned: UserDto? = null) =
            RoleSlotDto(role = role.toApiInt(), assignedUser = assigned)

        fun slots(arbitres: Int, withChrono: Boolean = true, withMar: Boolean = true) =
            buildList {
                if (arbitres >= 1) add(slot(OfficialRole.ARBITRE_1))
                if (arbitres >= 2) add(slot(OfficialRole.ARBITRE_2, officialUser))
                if (arbitres >= 3) add(slot(OfficialRole.ARBITRE_3))
                if (arbitres >= 4) add(slot(OfficialRole.ARBITRE_4))
                if (withChrono) add(slot(OfficialRole.CHRONO))
                if (withMar)    add(slot(OfficialRole.MAR))
            }

        return listOf(
            // ── This month ────────────────────────────────────────────────────
            MatchDto(
                id = "m01",
                division = DivisionDto("div-u17", "U17"),
                homeTeam = team("t01"), awayTeam = team("t02"),
                dateUtc = isoDate(y, m, 5, 15),
                location = location("loc-01"),
                slots = slots(2),
                currentUserStatus = 2,
            ),
            MatchDto(
                id = "m02",
                division = DivisionDto("div-u15", "U15"),
                homeTeam = team("t05"), awayTeam = team("t06"),
                dateUtc = isoDate(y, m, 10, 14),
                location = location("loc-02"),
                slots = slots(2),
                currentUserStatus = 0,
            ),
            MatchDto(
                id = "m03",
                division = DivisionDto("div-u13", "U13"),
                homeTeam = team("t09"), awayTeam = team("t10"),
                dateUtc = isoDate(y, m, 10, 16),
                location = location("loc-03"),
                slots = slots(2),
                currentUserStatus = 1,
            ),
            MatchDto(
                id = "m04",
                division = DivisionDto("div-u17", "U17"),
                homeTeam = team("t03"), awayTeam = team("t04"),
                dateUtc = isoDate(y, m, 17, 15),
                location = location("loc-04"),
                slots = slots(4),
                currentUserStatus = 4,
            ),
            MatchDto(
                id = "m05",
                division = DivisionDto("div-u15", "U15"),
                homeTeam = team("t07"), awayTeam = team("t08"),
                dateUtc = isoDate(y, m, 22, 14),
                location = location("loc-05"),
                slots = slots(2),
                currentUserStatus = 3,
            ),
            MatchDto(
                id = "m06",
                division = DivisionDto("div-u13", "U13"),
                homeTeam = team("t11"), awayTeam = team("t12"),
                dateUtc = isoDate(y, m, 28, 10),
                location = location("loc-06"),
                slots = slots(2),
                currentUserStatus = 0,
            ),
            MatchDto(
                id = "m07",
                division = DivisionDto("div-u17", "U17"),
                homeTeam = team("t02"), awayTeam = team("t03"),
                dateUtc = isoDate(y, m, 22, 17),
                location = location("loc-07"),
                slots = slots(2),
                currentUserStatus = 0,
            ),
            // ── Next month ────────────────────────────────────────────────────
            MatchDto(
                id = "m08",
                division = DivisionDto("div-u17", "U17"),
                homeTeam = team("t01"), awayTeam = team("t04"),
                dateUtc = isoDate(ny, nm, 8, 15),
                location = location("loc-01"),
                slots = slots(2),
                currentUserStatus = 0,
            ),
            MatchDto(
                id = "m09",
                division = DivisionDto("div-u15", "U15"),
                homeTeam = team("t06"), awayTeam = team("t07"),
                dateUtc = isoDate(ny, nm, 14, 14),
                location = location("loc-02"),
                slots = slots(2),
                currentUserStatus = 0,
            ),
            MatchDto(
                id = "m10",
                division = DivisionDto("div-u13", "U13"),
                homeTeam = team("t09"), awayTeam = team("t12"),
                dateUtc = isoDate(ny, nm, 20, 16),
                location = location("loc-03"),
                slots = slots(2),
                currentUserStatus = 0,
            ),
        )
    }

    // Notifications — type is now integer (NotificationType as int): 0=J15Reminder, 1=J4Reminder, 2=Emergency, 3=General
    val notifications = mutableListOf(
        NotificationDto(
            id = "n1", type = 0, // J15Reminder
            title = "Confirmation J-15 requise",
            body  = "Confirmez votre présence pour U17 Panthers vs Lions le 8 ${currentMonthName()}",
            matchId = "m08",
            createdAt = offsetDate(1),
            isRead = false,
        ),
        NotificationDto(
            id = "n2", type = 0, // J15Reminder
            title = "Confirmation J-15 requise",
            body  = "Confirmez votre présence pour U15 Hawks vs Bears le 14 ${currentMonthName()}",
            matchId = "m09",
            createdAt = offsetDate(2),
            isRead = false,
        ),
        NotificationDto(
            id = "n3", type = 1, // J4Reminder
            title = "Confirmation finale J-4",
            body  = "Confirmation finale requise pour U15 Titans vs Hawks",
            matchId = "m05",
            createdAt = offsetDate(4),
            isRead = false,
        ),
        NotificationDto(
            id = "n4", type = 3, // General
            title = "Bienvenue sur BasketballRef",
            body  = "Votre compte arbitre est actif. Bonne saison !",
            matchId = null,
            createdAt = offsetDate(30),
            isRead = true,
            isShowAtStart = true,
            isRecursif = false,
        ),
        NotificationDto(
            id = "n5", type = 2, // Emergency
            title = "Besoin urgent d'arbitres",
            body  = "Match U13 Rockets vs Comets le 10 — poste MAR non pourvu",
            matchId = "m03",
            createdAt = offsetDate(5),
            isRead = true,
        ),
    )

    val pointRules = OfficialRole.entries.mapIndexed { i, role ->
        PointRuleDto(
            id = "pr-$i",
            role = role.toApiInt(),  // API now sends integer
            pointsOnJ15 = 10,
            pointsOnJ4  = 5,
        )
    }
}