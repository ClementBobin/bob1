package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class GetMatchUseCase @Inject constructor(private val repo: MatchRepository) { suspend operator fun invoke(id: String) = repo.getMatch(id) }
