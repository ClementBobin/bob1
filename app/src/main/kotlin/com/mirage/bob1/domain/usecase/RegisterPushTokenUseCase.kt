package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class RegisterPushTokenUseCase @Inject constructor(private val repo: PushRepository) {
    suspend operator fun invoke(token: String? = null) =
        if (token == null) repo.registerCurrentToken() else repo.registerToken(token)
}
