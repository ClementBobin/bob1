package com.mirage.bob1.domain.model

import com.mirage.bob1.data.dto.OfficialRole

data class PointRule(
    val id: String,
    val role: OfficialRole,
    val pointsOnJ15: Int,
    val pointsOnJ4: Int,
)