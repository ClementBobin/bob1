package org.bob1.app.domain.usecase

import javax.inject.Inject
import org.bob1.app.domain.repository.*

class RegisterPushTokenUseCase @Inject constructor(private val repo: PushRepository) {
    suspend operator fun invoke(token: String? = null) =
        if (token == null) repo.registerCurrentToken() else repo.registerToken(token)
}
