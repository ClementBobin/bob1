package com.mirage.bob1.domain.model

enum class CommitmentType {
    Presence, Volunteer, Staff, CarpoolDriver, CarpoolPassenger, Other;

    companion object {
        /** Maps the API's `type` string (`presence`, `carpool_driver`...). */
        fun fromApi(raw: String) = when (raw.lowercase()) {
            "presence" -> Presence
            "volunteer" -> Volunteer
            "staff" -> Staff
            "carpool_driver" -> CarpoolDriver
            "carpool_passenger" -> CarpoolPassenger
            else -> Other
        }
    }
}
