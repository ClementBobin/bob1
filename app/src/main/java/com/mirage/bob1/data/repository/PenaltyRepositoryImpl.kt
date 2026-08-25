package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.PenaltyAPI
import com.mirage.bob1.domain.model.Penalty
import com.mirage.bob1.domain.repository.PenaltyRepository

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
