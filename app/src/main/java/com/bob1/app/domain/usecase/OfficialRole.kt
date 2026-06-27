package com.bob1.app.domain.usecase

import com.bob1.app.data.dto.OfficialRole

fun OfficialRole.displayName() = when (this) {
    OfficialRole.ARBITRE_1 -> "Arbitre 1"
    OfficialRole.ARBITRE_2 -> "Arbitre 2"
    OfficialRole.ARBITRE_3 -> "Arbitre 3"
    OfficialRole.ARBITRE_4 -> "Arbitre 4"
    OfficialRole.CHRONO    -> "Chrono"
    OfficialRole.MAR       -> "MAR"
}