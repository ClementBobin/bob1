package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class ConfirmPresenceUseCase @Inject constructor(private val repo: MatchRepository, private val commitments: CommitmentRepository) {
    suspend operator fun invoke(matchId: String) = repo.confirmPresence(matchId).onSuccess { commitments.refresh() }
}
