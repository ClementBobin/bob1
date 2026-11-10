package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class RefreshCommitmentsUseCase @Inject constructor(private val repo: CommitmentRepository) { suspend operator fun invoke() = repo.refresh() }
