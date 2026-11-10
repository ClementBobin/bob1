package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class GetMeUseCase @Inject constructor(private val repo: AuthRepository) { suspend operator fun invoke() = repo.me() }
