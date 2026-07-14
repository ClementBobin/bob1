package com.bob1.app.data.repository

import com.bob1.app.data.remote.PenaltyAPI
import com.bob1.app.domain.model.Penalty
import com.bob1.app.domain.repository.PenaltyRepository

internal class PenaltyRepositoryImpl(
    private val penaltyAPI: PenaltyAPI
) : PenaltyRepository {

    override suspend fun getMyPenalties(): Result<List<Penalty>> = runCatching {
        penaltyAPI.getMyPenalties().map { it.toDomain() }
    }

    override suspend fun acknowledgePenalty(penaltyId: String): Result<Unit> = runCatching {
        penaltyAPI.acknowledgePenalty(penaltyId)
    }
}
