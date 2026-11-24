package org.bob1.capacity.biometric

import android.content.Context
import androidx.fragment.app.FragmentActivity
import dev.kindling.android.natif.BiometricConfig
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.android.natif.BiometricResult
import dev.kindling.android.natif.EncryptedData
import dev.kindling.android.natif.KeystoreConfig
import dev.kindling.android.natif.KeystoreHelper
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Holds the device's biometric sign-in token. The token is encrypted with an AES-GCM Android Keystore key that only
 * works after a fingerprint / face check (`setUserAuthenticationRequired`), so reading it back always shows the system
 * prompt. Thin facade over Kindling's [BiometricHelper] + [KeystoreHelper]: the app never touches them directly.
 *
 * Only the ciphertext, its IV and two non-secret ids live in SharedPreferences.
 */
class BiometricVault(context: Context) {

    /** Which account the stored token signs in, and the server-side id used to revoke it. Not secret. */
    data class Entry(val userId: String, val credentialId: String)

    private val appContext = context.applicationContext
    private val keystore = KeystoreHelper()
    private val helper = BiometricHelper(appContext, keystore)
    private val prefs = appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val keyConfig = KeystoreConfig.biometricProtected(KEY_ALIAS)
    private val promptConfig = BiometricConfig.strong(
        title = "Connexion à BOB1",
        subtitle = "Confirmez votre identité",
        keyAlias = KEY_ALIAS,
    )

    private val _entry = MutableStateFlow(readEntry())
    /** `null` = nothing stored: biometric sign-in is off on this device. */
    val entry: StateFlow<Entry?> = _entry.asStateFlow()

    private val _offerDismissed = MutableStateFlow(prefs.getBoolean(K_OFFER_DISMISSED, false))
    /** The person said "no thanks" to the post-login offer. */
    val offerDismissed: StateFlow<Boolean> = _offerDismissed.asStateFlow()

    /** Biometric hardware present AND at least one fingerprint / face registered (strong class, needed for the Keystore key). */
    fun isAvailable(): Boolean = helper.canAuthenticate(promptConfig)

    /** Shows the prompt, then stores [token] encrypted. Replaces whatever was stored before (with a brand-new key). */
    suspend fun enroll(activity: FragmentActivity, token: String, userId: String, credentialId: String): VaultResult<Unit> {
        clear() // fresh key: an older one may have been invalidated
        return when (val r = prompt(activity, plaintext = token)) {
            is BiometricResult.SuccessWithEncrypted -> {
                prefs.edit()
                    .putString(K_CIPHER, r.data.ciphertext)
                    .putString(K_IV, r.data.iv)
                    .putString(K_USER, userId)
                    .putString(K_CREDENTIAL, credentialId)
                    .apply()
                _entry.value = Entry(userId, credentialId)
                VaultResult.Success(Unit)
            }
            else -> failure(r)
        }
    }

    /** Shows the prompt and returns the token. An invalidated key wipes the vault. */
    suspend fun unlock(activity: FragmentActivity): VaultResult<String> {
        val cipher = prefs.getString(K_CIPHER, null)
        val iv = prefs.getString(K_IV, null)
        if (_entry.value == null || cipher == null || iv == null) return VaultResult.NotEnrolled
        return when (val r = prompt(activity, data = EncryptedData(cipher, iv))) {
            is BiometricResult.SuccessWithDecrypted -> VaultResult.Success(r.plaintext)
            else -> failure(r).also { if (it is VaultResult.Invalidated) clear() }
        }
    }

    /** Forgets the token and deletes the key. */
    fun clear() {
        runCatching { keystore.deleteKey(keyConfig) }
        prefs.edit().remove(K_CIPHER).remove(K_IV).remove(K_USER).remove(K_CREDENTIAL).apply()
        _entry.value = null
    }

    /** A different account signed in with its password: the stored token isn't theirs. */
    fun clearIfOtherUser(userId: String) {
        _entry.value?.let { if (it.userId != userId) clear() }
    }

    fun dismissOffer() {
        prefs.edit().putBoolean(K_OFFER_DISMISSED, true).apply()
        _offerDismissed.value = true
    }

    // BiometricPrompt must be created on the main thread. A wrong finger (Failed) leaves the system prompt open,
    // so only terminal results resume the coroutine.
    private suspend fun prompt(
        activity: FragmentActivity,
        plaintext: String? = null,
        data: EncryptedData? = null,
    ): BiometricResult = withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { cont ->
            helper.authenticate(activity, promptConfig, plaintext, data) { result ->
                if (result is BiometricResult.Failed) return@authenticate
                if (cont.isActive) cont.resume(result)
            }
        }
    }

    private fun failure(r: BiometricResult): VaultResult<Nothing> = when (r) {
        BiometricResult.Unavailable -> VaultResult.Unavailable
        BiometricResult.NoneEnrolled -> VaultResult.NoneEnrolled
        is BiometricResult.Error -> when {
            // KeystoreHelper could not even build the cipher: KeyPermanentlyInvalidatedException or a lost key.
            r.message.startsWith("Failed to initialize cipher") -> VaultResult.Invalidated
            // The system reports the negative button by its label.
            r.message == promptConfig.negativeButtonText -> VaultResult.Cancelled
            else -> VaultResult.Error(r.message)
        }
        else -> VaultResult.Error("Authentification biométrique impossible.")
    }

    private fun readEntry(): Entry? {
        val user = prefs.getString(K_USER, null)
        val credential = prefs.getString(K_CREDENTIAL, null)
        val complete = prefs.contains(K_CIPHER) && prefs.contains(K_IV)
        return if (user != null && credential != null && complete) Entry(user, credential) else null
    }

    private companion object {
        const val PREFS = "bob1_biometric"
        const val KEY_ALIAS = "bob1_biometric_token"
        const val K_CIPHER = "cipher"
        const val K_IV = "iv"
        const val K_USER = "user_id"
        const val K_CREDENTIAL = "credential_id"
        const val K_OFFER_DISMISSED = "offer_dismissed"
    }
}
