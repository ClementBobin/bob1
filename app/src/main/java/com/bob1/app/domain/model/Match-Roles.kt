package com.bob1.app.domain.model

import com.bob1.app.data.dto.OfficialRole

data class RoleSlot(
    val role: OfficialRole,
    val assignedUserId: String? = null,
    val assignedUserName: String? = null,
)