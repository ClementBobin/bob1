package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class GetMeUseCase @Inject constructor(private val repo: AuthRepository) { suspend operator fun invoke() = repo.me() }
