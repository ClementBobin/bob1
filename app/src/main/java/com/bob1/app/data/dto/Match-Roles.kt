package com.bob1.app.data.dto

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
}
