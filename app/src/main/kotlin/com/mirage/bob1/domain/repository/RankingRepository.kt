package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.*

interface RankingRepository {
    suspend fun ranking(family: Boolean, category: String?): Result<Ranking>
}
