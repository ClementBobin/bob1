package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class GetRidesUseCase @Inject constructor(private val repo: CarpoolRepository) { suspend operator fun invoke(matchId: String) = repo.rides(matchId) }
