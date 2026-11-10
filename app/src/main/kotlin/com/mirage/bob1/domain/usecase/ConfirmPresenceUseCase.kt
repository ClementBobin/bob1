package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ConfirmPresenceUseCase @Inject constructor(private val repo: MatchRepository, private val commitments: CommitmentRepository) {
    suspend operator fun invoke(matchId: String) = repo.confirmPresence(matchId).onSuccess { commitments.refresh() }
}
