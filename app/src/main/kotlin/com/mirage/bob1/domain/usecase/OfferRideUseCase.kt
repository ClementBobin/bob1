package org.bob1.app.domain.usecase

import java.time.Instant
import javax.inject.Inject
import org.bob1.app.domain.repository.*

class OfferRideUseCase @Inject constructor(private val repo: CarpoolRepository, private val commitments: CommitmentRepository) {
    suspend operator fun invoke(matchId: String, seats: Int, departure: Instant, notes: String?) =
        repo.offerRide(matchId, seats, departure, notes?.trim()?.ifBlank { null }).onSuccess { commitments.refresh() }
}
