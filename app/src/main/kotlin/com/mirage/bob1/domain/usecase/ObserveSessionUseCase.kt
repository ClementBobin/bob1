package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class ObserveSessionUseCase @Inject constructor(private val repo: AuthRepository) { operator fun invoke() = repo.isAuthenticated }
