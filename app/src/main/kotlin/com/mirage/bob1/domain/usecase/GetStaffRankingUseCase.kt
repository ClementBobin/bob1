package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.RankingRepository

class GetStaffRankingUseCase @Inject constructor(private val repo: RankingRepository) { suspend operator fun invoke() = repo.ranking(family = false, category = "staff") }
