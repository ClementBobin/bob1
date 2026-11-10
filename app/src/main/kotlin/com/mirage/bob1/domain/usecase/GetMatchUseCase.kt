package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class GetMatchUseCase @Inject constructor(private val repo: MatchRepository) { suspend operator fun invoke(id: String) = repo.getMatch(id) }
