package com.mirage.bob1.data.dto

// API now sends MatchSubscriptionStatus as integer (type: integer in OpenAPI spec).
// Integer mapping: 0=Neutral, 1=Subscribed, 2=ConfirmedJ15, 3=ConfirmedJ4, 4=Full
// fromApiString kept for mock compatibility.
enum class MatchSubscriptionStatus {
    NEUTRAL,
    SUBSCRIBED,
    CONFIRMED_J15,
    CONFIRMED_J4,
    FULL;

    companion object {
        fun fromApiInt(value: Int): MatchSubscriptionStatus = when (value) {
            0    -> NEUTRAL
            1    -> SUBSCRIBED
            2    -> CONFIRMED_J15
            3    -> CONFIRMED_J4
            4    -> FULL
            else -> NEUTRAL
        }

        fun fromApiString(value: String): MatchSubscriptionStatus = when (value.lowercase()) {
            "neutral"      -> NEUTRAL
            "subscribed"   -> SUBSCRIBED
            "confirmedj15" -> CONFIRMED_J15
            "confirmedj4"  -> CONFIRMED_J4
            "full"         -> FULL
            else           -> NEUTRAL
        }
    }
}