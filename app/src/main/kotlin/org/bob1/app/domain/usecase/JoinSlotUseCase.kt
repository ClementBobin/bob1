package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class JoinSlotUseCase @Inject constructor(private val repo: MatchRepository, private val commitments: CommitmentRepository) {
    suspend operator fun invoke(slotId: String) = repo.joinSlot(slotId).onSuccess { commitments.refresh() }
}
