package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class GetVenueUseCase @Inject constructor(private val repo: ClubRepository) { suspend operator fun invoke(id: String) = repo.venue(id) }
