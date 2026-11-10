package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.model.Registration
import org.bob1.app.domain.repository.*

class RegisterUseCase @Inject constructor(private val repo: AuthRepository, private val push: PushRepository) {
    suspend operator fun invoke(form: Registration) = repo.register(
        form.copy(
            firstName = form.firstName.trim(), lastName = form.lastName.trim(), email = form.email.trim(),
            familyName = form.familyName?.trim()?.ifBlank { null },
            invitationCode = form.invitationCode?.trim()?.uppercase()?.ifBlank { null },
        )
    ).onSuccess { push.registerCurrentToken() }
}
