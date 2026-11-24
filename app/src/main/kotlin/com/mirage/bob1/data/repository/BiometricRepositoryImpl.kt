package com.mirage.bob1.data.repository

import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.mirage.bob1.data.remote.AccountAPI
import com.mirage.bob1.data.remote.AuthAPI
import com.mirage.bob1.data.remote.HttpException
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.BiometricException
import com.mirage.bob1.domain.model.BiometricStatus
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.repository.BiometricRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.bob1.capacity.biometric.BiometricVault
import org.bob1.capacity.biometric.VaultResult
import org.bob1.capacity.session.SecureSession

@Singleton
internal class BiometricRepositoryImpl @Inject constructor(
    private val authAPI: AuthAPI,
    private val accountAPI: AccountAPI,
    private val vault: BiometricVault,
    private val session: SecureSession,
) : BiometricRepository {

    override val status: Flow<BiometricStatus> =
        combine(vault.entry, vault.offerDismissed) { entry, dismissed ->
            BiometricStatus(available = vault.isAvailable(), enrolled = entry != null, offerDismissed = dismissed)
        }

    override suspend fun enable(activity: FragmentActivity): Result<Unit> = apiCall {
        if (!vault.isAvailable()) throw BiometricException.Unavailable()

        val me = accountAPI.getMe()
        // Re-enabling: the previous device token is replaced, so retire it on the server.
        vault.entry.value?.let { old -> runCatching { accountAPI.disableBiometric(old.credentialId) } }

        val enrollment = accountAPI.enrollBiometric(Build.MODEL).toDomain()
        when (val result = vault.enroll(activity, enrollment.token, me.id, enrollment.credentialId)) {
            is VaultResult.Success -> Unit
            else -> {
                // Prompt cancelled or failed: don't leave a credential on the server that no device holds.
                runCatching { accountAPI.disableBiometric(enrollment.credentialId) }
                throw result.toException()
            }
        }
    }

    override suspend fun login(activity: FragmentActivity): Result<User> = apiCall {
        val token = when (val result = vault.unlock(activity)) {
            is VaultResult.Success -> result.value
            else -> throw result.toException()
        }
        try {
            val response = authAPI.biometricLogin(token)
            session.save(response.accessToken, response.refreshToken)
            response.user.toDomain()
        } catch (e: HttpException.ClientError) {
            // 401 on this anonymous call = revoked / expired / password changed: the stored token is useless now.
            if (e.statusCode == 401) vault.clear()
            throw e
        }
    }

    override suspend fun disable(): Result<Unit> = apiCall {
        vault.entry.value?.let { entry -> runCatching { accountAPI.disableBiometric(entry.credentialId) } }
        vault.clear()
    }

    override fun dismissOffer() = vault.dismissOffer()

    private fun VaultResult<*>.toException(): BiometricException = when (this) {
        VaultResult.Cancelled -> BiometricException.Cancelled()
        VaultResult.Unavailable -> BiometricException.Unavailable()
        VaultResult.NoneEnrolled -> BiometricException.NoneEnrolled()
        VaultResult.NotEnrolled -> BiometricException.NotEnrolled()
        VaultResult.Invalidated -> BiometricException.Invalidated()
        is VaultResult.Error -> BiometricException.Failed(message)
        is VaultResult.Success -> BiometricException.Failed("Opération biométrique inattendue.")
    }
}
