package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class JoinFamilyUseCase @Inject constructor(private val repo: FamilyRepository) {
    suspend operator fun invoke(code: String) = repo.join(code)
}
