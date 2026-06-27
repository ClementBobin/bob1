package com.bob1.app.domain.usecase

import androidx.compose.ui.graphics.Color
import com.bob1.app.data.dto.MatchSubscriptionStatus

fun MatchSubscriptionStatus.dotColor(): Color = when (this) {
    MatchSubscriptionStatus.NEUTRAL       -> Color(0xFF9E9E9E)
    MatchSubscriptionStatus.SUBSCRIBED    -> Color(0xFF2196F3)
    MatchSubscriptionStatus.CONFIRMED_J15 -> Color(0xFFFF9800)
    MatchSubscriptionStatus.CONFIRMED_J4  -> Color(0xFF4CAF50)
    MatchSubscriptionStatus.FULL          -> Color(0xFFF44336)
}

fun MatchSubscriptionStatus.label(): String = when (this) {
    MatchSubscriptionStatus.NEUTRAL       -> "Non inscrit"
    MatchSubscriptionStatus.SUBSCRIBED    -> "Inscrit"
    MatchSubscriptionStatus.CONFIRMED_J15 -> "Confirmé J-15"
    MatchSubscriptionStatus.CONFIRMED_J4  -> "Confirmé J-4"
    MatchSubscriptionStatus.FULL          -> "Complet"
}