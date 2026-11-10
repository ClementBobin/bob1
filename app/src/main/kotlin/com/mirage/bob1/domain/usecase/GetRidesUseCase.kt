package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class GetRidesUseCase @Inject constructor(private val repo: CarpoolRepository) { suspend operator fun invoke(matchId: String) = repo.rides(matchId) }
