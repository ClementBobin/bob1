package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class BiometricEnrollRequestDto(val deviceName: String? = null)
