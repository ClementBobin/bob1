package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ClubQueryDto(
    val search: String? = null,
    val isActive: Boolean? = null,
    val skip: Int? = null,
    val take: Int? = null,
)
