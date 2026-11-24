package com.mirage.bob1.domain.usecase

import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class ObserveBiometricStatusUseCase @Inject constructor(private val repo: BiometricRepository) { operator fun invoke() = repo.status }
