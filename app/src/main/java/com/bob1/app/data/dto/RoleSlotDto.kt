package com.bob1.app.data.dto

import com.bob1.app.domain.model.RoleSlot
import kotlinx.serialization.Serializable

@Serializable
data class RoleSlotDto(
    val role: String,
    val assignedUserId: String? = null,
    val assignedUserName: String? = null,
) {
    fun toDomain() = RoleSlot(
        role = OfficialRole.valueOf(role),
        assignedUserId = assignedUserId,
        assignedUserName = assignedUserName,
    )

    companion object {
        fun fromDomain(rs: RoleSlot) = RoleSlotDto(
            role = rs.role.name,
            assignedUserId = rs.assignedUserId,
            assignedUserName = rs.assignedUserName
        )
    }
}