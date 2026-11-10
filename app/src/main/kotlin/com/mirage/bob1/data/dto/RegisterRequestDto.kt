package com.mirage.bob1ob1ob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1ob1ob1.domain.model.*

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val familyName: String? = null,
    val invitationCode: String? = null,
) {
    companion object {
        fun fromDomain(r: Registration) = RegisterRequestDto(
            email = r.email,
            password = r.password,
            firstName = r.firstName,
            lastName = r.lastName,
            familyName = r.familyName,
            invitationCode = r.invitationCode,
        )
    }
}
