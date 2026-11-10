package org.bob1.app.domain.repository

import org.bob1.app.domain.model.*

interface RankingRepository {
    suspend fun ranking(family: Boolean, category: String?): Result<Ranking>
}
