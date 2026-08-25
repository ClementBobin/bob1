package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.Penalty

interface PenaltyRepository {
    suspend fun getMyPenalties(): Result<List<Penalty>>
    suspend fun acknowledgePenalty(penaltyId: String): Result<Unit>
}