package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class LoginUseCase @Inject constructor(private val repo: AuthRepository, private val push: PushRepository) {
    suspend operator fun invoke(email: String, password: String) =
        repo.login(email.trim(), password).onSuccess { push.registerCurrentToken() }
}
