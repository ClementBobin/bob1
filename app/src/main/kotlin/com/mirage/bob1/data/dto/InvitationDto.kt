package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1.domain.model.*

@Serializable
data class InvitationDto(val code: String, val expiresAtUtc: String) {
    fun toDomain() = Invitation(code, parseUtc(expiresAtUtc))
}
