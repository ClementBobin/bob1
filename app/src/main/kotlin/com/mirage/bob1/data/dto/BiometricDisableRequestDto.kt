package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

/** No [credentialId] = sign out every enrolled device. */
@Serializable
data class BiometricDisableRequestDto(val credentialId: String? = null)
