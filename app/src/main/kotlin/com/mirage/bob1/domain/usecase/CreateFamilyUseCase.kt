package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class CreateFamilyUseCase @Inject constructor(private val repo: FamilyRepository) {
    suspend operator fun invoke(name: String?) = repo.create(name)
}
