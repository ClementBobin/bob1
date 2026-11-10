package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.RankingAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Ranking
import com.mirage.bob1.domain.repository.RankingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RankingRepositoryImpl @Inject constructor(
    private val rankingAPI: RankingAPI,
) : RankingRepository {

    override suspend fun ranking(family: Boolean, category: String?): Result<Ranking> = apiCall {
        if (family) rankingAPI.getFamilyRanking(category).toDomain()
        else rankingAPI.getPersonRanking(category).toDomain()
    }
}
