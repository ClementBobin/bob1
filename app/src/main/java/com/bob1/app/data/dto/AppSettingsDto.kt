package com.bob1.app.data.dto

import com.bob1.app.domain.model.AppSettings
import kotlinx.serialization.Serializable

@Serializable
data class AppSettingsDto(
    val confirmationOffsetJ15: Int,
    val confirmationOffsetJ4: Int,
) {
    fun toDomain() = AppSettings(
        confirmationOffsetJ15 = confirmationOffsetJ15,
        confirmationOffsetJ4 = confirmationOffsetJ4,
    )

    companion object {
        fun fromDomain(s: AppSettings) = AppSettingsDto(
            confirmationOffsetJ15 = s.confirmationOffsetJ15,
            confirmationOffsetJ4 = s.confirmationOffsetJ4
        )
    }
}