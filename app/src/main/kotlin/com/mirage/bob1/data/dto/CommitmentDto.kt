package com.mirage.bob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1.domain.model.*

@Serializable
data class CommitmentDto(
    val type: String,
    val matchId: String,
    val matchTitle: String,
    val startsAtUtc: String,
    val label: String = "",
) {
    fun toDomain() = Commitment(CommitmentType.fromApi(type), matchId, matchTitle, parseUtc(startsAtUtc), label)
}
