package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.model.Registration
import com.mirage.bob1.domain.repository.*

class RegisterUseCase @Inject constructor(private val repo: AuthRepository, private val push: PushRepository) {
    suspend operator fun invoke(form: Registration) = repo.register(
        form.copy(
            firstName = form.firstName.trim(), lastName = form.lastName.trim(), email = form.email.trim(),
            familyName = form.familyName?.trim()?.ifBlank { null },
            invitationCode = form.invitationCode?.trim()?.uppercase()?.ifBlank { null },
        )
    ).onSuccess { push.registerCurrentToken() }
}
