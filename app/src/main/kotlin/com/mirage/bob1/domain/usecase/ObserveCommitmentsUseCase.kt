package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class ObserveCommitmentsUseCase @Inject constructor(private val repo: CommitmentRepository) { operator fun invoke() = repo.observe() }
