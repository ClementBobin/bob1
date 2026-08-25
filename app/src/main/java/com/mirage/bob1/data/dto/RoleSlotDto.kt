package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.RoleSlot
import kotlinx.serialization.Serializable

@Serializable
data class RoleSlotDto(
    val role: Int,                     // API sends OfficialRole as integer
    val assignedUser: UserDto? = null, // API sends nested user object
) {
    fun toDomain() = RoleSlot(
        role             = OfficialRole.fromApiInt(role),
        assignedUserId   = assignedUser?.id,
        assignedUserName = assignedUser?.let { "${it.firstName} ${it.lastName}" },
    )
}