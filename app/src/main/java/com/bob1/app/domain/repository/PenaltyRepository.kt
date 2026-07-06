package com.bob1.app.domain.repository

import com.bob1.app.domain.model.Penalty

interface PenaltyRepository {
    suspend fun getMyPenalties(): Result<List<Penalty>>
    suspend fun acknowledgePenalty(penaltyId: String): Result<Unit>
}