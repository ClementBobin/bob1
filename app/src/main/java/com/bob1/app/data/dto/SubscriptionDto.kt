package com.bob1.app.data.dto

enum class MatchSubscriptionStatus {
    NEUTRAL,       // not subscribed
    SUBSCRIBED,    // initial subscription
    CONFIRMED_J15, // confirmed at J-15
    CONFIRMED_J4,  // confirmed at J-4
    FULL,          // all slots taken
}
