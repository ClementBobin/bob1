package com.bob1.app.data.dto

// API sends Pascal case: "Subscribed", "ConfirmedJ15", "ConfirmedJ4", "Full", "Neutral"
enum class MatchSubscriptionStatus {
    NEUTRAL,
    SUBSCRIBED,
    CONFIRMED_J15,
    CONFIRMED_J4,
    FULL;

    companion object {
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
