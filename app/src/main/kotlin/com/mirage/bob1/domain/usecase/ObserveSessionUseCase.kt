package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ObserveSessionUseCase @Inject constructor(private val repo: AuthRepository) { operator fun invoke() = repo.isAuthenticated }
