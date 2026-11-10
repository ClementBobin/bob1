package com.mirage.bob1.domain.usecase

import java.time.Instant
import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class RefreshMatchesUseCase @Inject constructor(private val repo: MatchRepository) { suspend operator fun invoke(from: Instant, to: Instant) = repo.refreshMatches(from, to) }
