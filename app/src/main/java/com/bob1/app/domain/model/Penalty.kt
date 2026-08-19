package com.bob1.app.domain.model

data class Penalty(
    val id: String,
    val seasonId: Int,
    val userId: String,
    val matchId: String?,
    val reason: String,
    val points: Int,
    val kickedOut: Boolean = false,
    val acknowledgedAt: String? = null,
    val timestampIso: String,
)