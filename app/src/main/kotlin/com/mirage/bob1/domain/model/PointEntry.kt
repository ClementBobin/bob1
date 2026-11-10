package org.bob1.app.domain.model

import java.time.Instant

data class PointEntry(
    val id: String,
    val category: String,
    val points: Int,
    val reason: String,
    val createdAt: Instant,
)
