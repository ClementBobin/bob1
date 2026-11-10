package org.bob1.app.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.dto.*
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.model.*
import org.bob1.app.domain.repository.*

@Singleton
class RankingRepositoryImpl @Inject constructor(private val api: Bob1Api) : RankingRepository {
    override suspend fun ranking(family: Boolean, category: String?): Result<Ranking> = apiCall {
        if (family) api.familyRanking(RankingQueryDto(view = "family", category = category, take = 50)).toDomain()
        else api.personRanking(RankingQueryDto(category = category, take = 50)).toDomain()
    }
}
