package com.bob1.app.data.dto

import com.bob1.app.domain.model.PointRule
import kotlinx.serialization.Serializable

@Serializable
data class PointRuleDto(
    val id: String,
    val role: String,
    val pointsOnJ15: Int,
    val pointsOnJ4: Int,
    val pointsEmergency: Int,
) {
    fun toDomain() = PointRule(
        id = id,
        role = OfficialRole.valueOf(role),
        pointsOnJ15 = pointsOnJ15,
        pointsOnJ4 = pointsOnJ4,
        pointsEmergency = pointsEmergency,
    )

    companion object {
        fun fromDomain(pr: PointRule) = PointRuleDto(
            id = pr.id,
            role = pr.role.name,
            pointsOnJ15 = pr.pointsOnJ15,
            pointsOnJ4 = pr.pointsOnJ4,
            pointsEmergency = pr.pointsEmergency
        )
    }
}
