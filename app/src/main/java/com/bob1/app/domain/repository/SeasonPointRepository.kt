package com.bob1.app.domain.repository

import com.bob1.app.domain.model.SeasonPointRanking
import com.bob1.app.domain.model.SeasonPointSummary

interface SeasonPointRepository {
    suspend fun getRanking(season: Int? = null): Result<List<SeasonPointRanking>>
    suspend fun getMyRanking(season: Int? = null): Result<SeasonPointSummary>
    suspend fun getUserRanking(userId: String, season: Int? = null): Result<SeasonPointSummary>
}