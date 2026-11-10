package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.RankingRepository

class GetStaffRankingUseCase @Inject constructor(private val repo: RankingRepository) { suspend operator fun invoke() = repo.ranking(family = false, category = "staff") }
