package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class GetVenueUseCase @Inject constructor(private val repo: ClubRepository) { suspend operator fun invoke(id: String) = repo.venue(id) }
