package com.mirage.bob1.domain.usecase

import com.mirage.bob1.domain.repository.ClubRepository
import javax.inject.Inject

class GetTeamsUseCase @Inject constructor(private val repo: ClubRepository) { suspend operator fun invoke() = repo.teams() }
