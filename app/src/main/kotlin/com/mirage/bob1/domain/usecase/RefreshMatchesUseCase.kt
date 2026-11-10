package org.bob1.app.domain.usecase

import java.time.Instant
import javax.inject.Inject
import org.bob1.app.domain.repository.*

class RefreshMatchesUseCase @Inject constructor(private val repo: MatchRepository) { suspend operator fun invoke(from: Instant, to: Instant) = repo.refreshMatches(from, to) }
