package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ObserveCommitmentsUseCase @Inject constructor(private val repo: CommitmentRepository) { operator fun invoke() = repo.observe() }
