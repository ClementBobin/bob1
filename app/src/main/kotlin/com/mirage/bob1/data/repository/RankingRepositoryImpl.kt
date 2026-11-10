package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.*
import com.mirage.bob1.domain.repository.*

@Singleton
class RankingRepositoryImpl @Inject constructor(private val api: Bob1Api) : RankingRepository {
    override suspend fun ranking(family: Boolean, category: String?): Result<Ranking> = apiCall {
        if (family) api.familyRanking(RankingQueryDto(view = "family", category = category, take = 50)).toDomain()
        else api.personRanking(RankingQueryDto(category = category, take = 50)).toDomain()
    }
}
