package com.mirage.bob1.domain.usecase

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class LoginWithBiometricUseCase @Inject constructor(private val repo: BiometricRepository, private val push: PushRepository) {
    suspend operator fun invoke(activity: FragmentActivity) =
        repo.login(activity).onSuccess { push.registerCurrentToken() }
}
