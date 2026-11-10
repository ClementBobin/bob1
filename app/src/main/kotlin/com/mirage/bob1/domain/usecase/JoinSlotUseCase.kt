package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class JoinSlotUseCase @Inject constructor(private val repo: MatchRepository, private val commitments: CommitmentRepository) {
    suspend operator fun invoke(slotId: String) = repo.joinSlot(slotId).onSuccess { commitments.refresh() }
}
