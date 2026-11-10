package org.bob1.app.domain.usecase

import java.time.Instant
import javax.inject.Inject
import org.bob1.app.domain.repository.*

class ObserveMatchesUseCase @Inject constructor(private val repo: MatchRepository) { operator fun invoke(from: Instant, to: Instant) = repo.observeMatches(from, to) }
