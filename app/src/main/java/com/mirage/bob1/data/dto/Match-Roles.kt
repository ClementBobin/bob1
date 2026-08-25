package com.mirage.bob1.data.dto

// API sends OfficialRole as integer (type: integer in OpenAPI spec).
// Legacy mock still uses Pascal-case strings; fromApiString kept for compatibility.
// Integer mapping matches server enum order: 0=Arbitre1, 1=Arbitre2, 2=Arbitre3, 3=Arbitre4, 4=Chrono, 5=Mar
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

    /** Serialise vers le format attendu par l'API (ex. "Arbitre1"). Used in subscribe request body. */
    fun toApiString(): String = when (this) {
        ARBITRE_1 -> "Arbitre1"
        ARBITRE_2 -> "Arbitre2"
        ARBITRE_3 -> "Arbitre3"
        ARBITRE_4 -> "Arbitre4"
        CHRONO    -> "Chrono"
        MAR       -> "Mar"
    }

    /** Serialise vers l'entier attendu par l'API pour les champs role (PointRuleDto, etc.). */
    fun toApiInt(): Int = when (this) {
        ARBITRE_1 -> 0
        ARBITRE_2 -> 1
        ARBITRE_3 -> 2
        ARBITRE_4 -> 3
        CHRONO    -> 4
        MAR       -> 5
    }

    companion object {
        /** Désérialise depuis un entier API. */
        fun fromApiInt(value: Int): OfficialRole = when (value) {
            0    -> ARBITRE_1
            1    -> ARBITRE_2
            2    -> ARBITRE_3
            3    -> ARBITRE_4
            4    -> CHRONO
            5    -> MAR
            else -> throw IllegalArgumentException("Unknown OfficialRole int: $value")
        }

        /** Désérialise depuis le format API chaîne (insensible à la casse). Used by mock. */
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