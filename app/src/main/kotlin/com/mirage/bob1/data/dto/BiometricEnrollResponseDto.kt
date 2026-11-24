package com.mirage.bob1.data.dto

import com.mirage.bob1.domain.model.BiometricEnrollment
import kotlinx.serialization.Serializable

@Serializable
data class BiometricEnrollResponseDto(
    val biometricToken: String,
    val credentialId: String,
) {
    fun toDomain() = BiometricEnrollment(
        token = biometricToken,
        credentialId = credentialId,
    )

    companion object {
        fun fromDomain(e: BiometricEnrollment) = BiometricEnrollResponseDto(
            biometricToken = e.token,
            credentialId = e.credentialId,
        )
    }
}
