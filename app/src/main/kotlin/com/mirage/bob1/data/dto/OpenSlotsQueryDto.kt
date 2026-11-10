package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OpenSlotsQueryDto(val from: String? = null, val to: String? = null)
