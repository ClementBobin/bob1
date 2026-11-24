package com.mirage.bob1.domain.repository

import androidx.fragment.app.FragmentActivity
import com.mirage.bob1.domain.model.BiometricStatus
import com.mirage.bob1.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Biometric sign-in. The system prompt needs a [FragmentActivity].
 * Failures are `BiometricException` (prompt side) or `HttpException` (see data/remote).
 */
interface BiometricRepository {
    val status: Flow<BiometricStatus>

    /** Signed in: enroll this device (asks the API for a device token, then locks it behind the biometric prompt). */
    suspend fun enable(activity: FragmentActivity): Result<Unit>

    /** Signed out: unlock the device token with the biometric prompt and trade it for a session. */
    suspend fun login(activity: FragmentActivity): Result<User>

    suspend fun disable(): Result<Unit>

    /** "No thanks" to the post-login offer; it can still be enabled from the profile. */
    fun dismissOffer()
}
