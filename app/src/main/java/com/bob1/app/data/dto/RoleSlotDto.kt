package com.bob1.app.data.dto

import com.bob1.app.domain.model.RoleSlot
import kotlinx.serialization.Serializable

@Serializable
data class RoleSlotDto(
    val role: String,
    val assignedUser: UserDto? = null, // API sends nested user object
) {
    fun toDomain() = RoleSlot(
        role             = OfficialRole.fromApiString(role),
        assignedUserId   = assignedUser?.id,
        assignedUserName = assignedUser?.let { "${it.firstName} ${it.lastName}" },
    )
}
