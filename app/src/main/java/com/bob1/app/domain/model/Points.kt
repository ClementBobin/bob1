package com.bob1.app.domain.model

import com.bob1.app.data.dto.OfficialRole

data class PointRule(
    val id: String,
    val role: OfficialRole,
    val pointsOnJ15: Int,
    val pointsOnJ4: Int,
    val pointsEmergency: Int,
)
