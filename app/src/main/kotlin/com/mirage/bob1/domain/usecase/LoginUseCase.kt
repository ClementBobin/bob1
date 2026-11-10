package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class LoginUseCase @Inject constructor(private val repo: AuthRepository, private val push: PushRepository) {
    suspend operator fun invoke(email: String, password: String) =
        repo.login(email.trim(), password).onSuccess { push.registerCurrentToken() }
}
