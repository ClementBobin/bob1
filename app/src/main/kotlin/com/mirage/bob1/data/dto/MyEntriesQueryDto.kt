package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyEntriesQueryDto(val season: String? = null, val take: Int? = null)
