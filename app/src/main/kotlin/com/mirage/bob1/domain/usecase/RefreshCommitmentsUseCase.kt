package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class RefreshCommitmentsUseCase @Inject constructor(private val repo: CommitmentRepository) { suspend operator fun invoke() = repo.refresh() }
