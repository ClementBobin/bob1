package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class BookRideUseCase @Inject constructor(private val repo: CarpoolRepository, private val commitments: CommitmentRepository) {
    suspend operator fun invoke(rideId: String) = repo.book(rideId).onSuccess { commitments.refresh() }
}
