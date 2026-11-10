package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

@Serializable
data class InvitationDto(val code: String, val expiresAtUtc: String) {
    fun toDomain() = Invitation(code, parseUtc(expiresAtUtc))
}
