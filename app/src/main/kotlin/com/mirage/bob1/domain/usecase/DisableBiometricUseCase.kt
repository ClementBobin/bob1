package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class DisableBiometricUseCase @Inject constructor(private val repo: BiometricRepository) { suspend operator fun invoke() = repo.disable() }
