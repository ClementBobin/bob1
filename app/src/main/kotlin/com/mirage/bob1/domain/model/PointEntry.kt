package com.mirage.bob1.domain.model

import java.time.Instant

data class PointEntry(
    val id: String,
    val category: String,
    val points: Int,
    val reason: String,
    val createdAt: Instant,
    val isManual: Boolean = false,
    val isRevoked: Boolean = false,
)
