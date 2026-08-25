package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.SeasonPointRanking
import com.mirage.bob1.domain.model.SeasonPointSummary

interface SeasonPointRepository {
    suspend fun getRanking(season: Int? = null): Result<List<SeasonPointRanking>>
    suspend fun getMyRanking(season: Int? = null): Result<SeasonPointSummary>
    suspend fun getUserRanking(userId: String, season: Int? = null): Result<SeasonPointSummary>
}