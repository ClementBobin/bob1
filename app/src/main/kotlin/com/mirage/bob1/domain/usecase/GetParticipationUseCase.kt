package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class GetParticipationUseCase @Inject constructor(private val repo: MatchRepository) { suspend operator fun invoke(matchId: String) = repo.participation(matchId) }
