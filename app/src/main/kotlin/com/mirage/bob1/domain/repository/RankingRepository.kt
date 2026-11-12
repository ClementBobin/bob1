package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.*

interface RankingRepository {
    /** [season] `null` = the current season. */
    suspend fun ranking(family: Boolean, category: String?, season: String? = null): Result<Ranking>
}
