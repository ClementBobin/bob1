package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class GetParticipationUseCase @Inject constructor(private val repo: MatchRepository) { suspend operator fun invoke(matchId: String) = repo.participation(matchId) }
