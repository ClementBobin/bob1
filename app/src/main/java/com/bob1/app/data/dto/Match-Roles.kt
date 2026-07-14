package com.bob1.app.data.dto

// API sends Pascal case: "Arbitre1", "Arbitre2", "Chrono", "Mar"
// App uses SCREAMING_SNAKE: ARBITRE_1, ARBITRE_2, CHRONO, MAR
enum class OfficialRole {
    ARBITRE_1, ARBITRE_2, ARBITRE_3, ARBITRE_4,
    CHRONO, MAR;

    fun displayName(): String = when (this) {
        ARBITRE_1 -> "Arbitre 1"
        ARBITRE_2 -> "Arbitre 2"
        ARBITRE_3 -> "Arbitre 3"
        ARBITRE_4 -> "Arbitre 4"
        CHRONO    -> "Chrono"
        MAR       -> "MAR"
    }

    /** Serialise vers le format attendu par l'API (ex. "Arbitre1"). */
    fun toApiString(): String = when (this) {
        ARBITRE_1 -> "Arbitre1"
        ARBITRE_2 -> "Arbitre2"
        ARBITRE_3 -> "Arbitre3"
        ARBITRE_4 -> "Arbitre4"
        CHRONO    -> "Chrono"
        MAR       -> "Mar"
    }

    companion object {
        /** Désérialise depuis le format API (insensible à la casse). */
        fun fromApiString(value: String): OfficialRole = when (value.lowercase()) {
            "arbitre1" -> ARBITRE_1
            "arbitre2" -> ARBITRE_2
            "arbitre3" -> ARBITRE_3
            "arbitre4" -> ARBITRE_4
            "chrono"   -> CHRONO
            "mar"      -> MAR
            else       -> throw IllegalArgumentException("Unknown OfficialRole: $value")
        }
    }
}
