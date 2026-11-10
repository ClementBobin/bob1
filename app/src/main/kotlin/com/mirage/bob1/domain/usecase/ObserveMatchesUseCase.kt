package com.mirage.bob1.domain.usecase

import java.time.Instant
import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ObserveMatchesUseCase @Inject constructor(private val repo: MatchRepository) { operator fun invoke(from: Instant, to: Instant) = repo.observeMatches(from, to) }
