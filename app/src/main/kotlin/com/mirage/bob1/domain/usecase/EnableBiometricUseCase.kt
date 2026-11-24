package com.mirage.bob1.domain.usecase

import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import com.mirage.bob1.domain.repository.*

class EnableBiometricUseCase @Inject constructor(private val repo: BiometricRepository) {
    suspend operator fun invoke(activity: FragmentActivity) = repo.enable(activity)
}
