package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationQueryDto(
    val unreadOnly: Boolean? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
