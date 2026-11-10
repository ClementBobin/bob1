package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class CreateInvitationUseCase @Inject constructor(private val repo: FamilyRepository) { suspend operator fun invoke() = repo.createInvitation() }
