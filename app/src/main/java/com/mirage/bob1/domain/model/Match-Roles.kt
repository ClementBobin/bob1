package com.mirage.bob1.domain.model

import com.mirage.bob1.data.dto.OfficialRole

data class RoleSlot(
    val role: OfficialRole,
    val assignedUserId: String? = null,
    val assignedUserName: String? = null,
)