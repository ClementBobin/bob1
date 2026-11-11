package com.mirage.bob1.ui.components

import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.model.MatchStatus

fun Match.tone(committed: Boolean): Tone = when {
    status == MatchStatus.Cancelled -> Tone.Danger
    status == MatchStatus.Played -> Tone.Neutral
    committed -> Tone.Joined
    else -> Tone.Pending
}

fun Match.statusLabel(committed: Boolean): String = when {
    status == MatchStatus.Cancelled -> "Annulé"
    status == MatchStatus.Played -> "Joué"
    committed -> "Inscrit"
    else -> "À confirmer"
}
