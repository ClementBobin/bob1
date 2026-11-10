package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class GetRankingUseCase @Inject constructor(private val repo: RankingRepository) { suspend operator fun invoke(family: Boolean, category: String? = null) = repo.ranking(family, category) }
