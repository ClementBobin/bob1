package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MyEntriesQueryDto(val season: String? = null, val take: Int? = null)
