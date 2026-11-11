package com.mirage.bob1.ui.components.commitments

import com.mirage.bob1.domain.model.Commitment
import com.mirage.bob1.domain.model.CommitmentType

fun Commitment.typeLabel() = when (type) {
    CommitmentType.Presence -> "Présence confirmée"
    CommitmentType.Volunteer -> "Bénévolat" + if (label.isNotBlank()) " · $label" else ""
    CommitmentType.Staff -> "Staff" + if (label.isNotBlank()) " · $label" else ""
    CommitmentType.CarpoolDriver -> "Conducteur"
    CommitmentType.CarpoolPassenger -> "Passager"
    CommitmentType.Other -> label.ifBlank { "Engagement" }
}
