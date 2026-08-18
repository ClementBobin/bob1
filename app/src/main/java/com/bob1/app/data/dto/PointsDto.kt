package com.bob1.app.data.dto

import com.bob1.app.domain.model.PointRule
import kotlinx.serialization.Serializable

@Serializable
data class PointRuleDto(
    val id: String,
    val role: Int,              // API now sends OfficialRole as integer
    val pointsOnJ15: Int,
    val pointsOnJ4: Int,
) {
    fun toDomain() = PointRule(
        id = id,
        role = OfficialRole.fromApiInt(role),
        pointsOnJ15 = pointsOnJ15,
        pointsOnJ4 = pointsOnJ4,
    )

    companion object {
        fun fromDomain(pr: PointRule) = PointRuleDto(
            id = pr.id,
            role = pr.role.toApiInt(),
            pointsOnJ15 = pr.pointsOnJ15,
            pointsOnJ4 = pr.pointsOnJ4,
        )
    }
}