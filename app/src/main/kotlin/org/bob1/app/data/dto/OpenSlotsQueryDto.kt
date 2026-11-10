package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenSlotsQueryDto(val from: String? = null, val to: String? = null)
