package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class GetRankingUseCase @Inject constructor(private val repo: RankingRepository) { suspend operator fun invoke(family: Boolean, category: String? = null, season: String? = null) = repo.ranking(family, category, season) }
