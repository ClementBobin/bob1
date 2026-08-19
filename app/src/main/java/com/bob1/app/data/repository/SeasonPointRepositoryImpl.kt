package com.bob1.app.data.repository

import com.bob1.app.data.remote.SeasonPointAPI
import com.bob1.app.domain.model.SeasonPointEntry
import com.bob1.app.domain.model.SeasonPointRanking
import com.bob1.app.domain.model.SeasonPointSummary
import com.bob1.app.domain.repository.SeasonPointRepository

internal class SeasonPointRepositoryImpl(
    private val api: SeasonPointAPI,
) : SeasonPointRepository {

    override suspend fun getRanking(season: Int?): Result<List<SeasonPointRanking>> = runCatching {
        api.getRanking(season).map { dto ->
            SeasonPointRanking(
                rank        = dto.rank,
                userId      = dto.userId,
                fullName    = dto.fullName,
                totalPoints = dto.totalPoints,
            )
        }
    }

    override suspend fun getMyRanking(season: Int?): Result<SeasonPointSummary> = runCatching {
        api.getMyRanking(season).toDomain()
    }

    override suspend fun getUserRanking(userId: String, season: Int?): Result<SeasonPointSummary> = runCatching {
        api.getUserRanking(userId, season).toDomain()
    }
}

private fun com.bob1.app.data.dto.SeasonPointSummaryDto.toDomain() = SeasonPointSummary(
    seasonId    = seasonId,
    userId      = userId,
    totalPoints = totalPoints,
    entries     = entries.map { e ->
        SeasonPointEntry(
            id        = e.id,
            seasonId  = e.seasonId,
            userId    = e.userId,
            matchId   = e.matchId,
            points    = e.points,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt,
        )
    },
)