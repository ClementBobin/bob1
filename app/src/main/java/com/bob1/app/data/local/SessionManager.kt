package com.bob1.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.bob1.app.data.dto.UserDto
import com.bob1.app.domain.model.User
import dev.kindling.android.natif.KeystoreHelper
import dev.kindling.android.natif.KeystoreConfig
import dev.kindling.android.natif.EncryptedData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

/**
 * Session manager backed by Kindling's [KeystoreHelper] for AES-256-GCM token
 * encryption. Tokens are encrypted before being stored in SharedPreferences and
 * decrypted on retrieval — keys never leave the Android Keystore hardware.
 *
 * Biometric credentials (email + password) are stored encrypted under a separate
 * key alias so [BiometricHelper] can trigger a real API login transparently.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bob1_secure_prefs", Context.MODE_PRIVATE)

    private val keystore        = KeystoreHelper()
    private val keystoreConfig  = KeystoreConfig.default("bob1_session_key")
    private val credConfig      = KeystoreConfig.default("bob1_biometric_creds_key")

    private val _user  = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    init { restoreSession() }

    private fun restoreSession() {
        runCatching {
            val encCiphertext = prefs.getString("token_ct", null) ?: return
            val encIv         = prefs.getString("token_iv", null) ?: return
            val token = keystore.decrypt(keystoreConfig, EncryptedData(encCiphertext, encIv))
            val userJson = prefs.getString("user_json", null)
            val user = userJson?.let { Json.decodeFromString<UserDto>(it).toDomain() }
            _token.value          = token
            _user.value           = user
            _biometricEnabled.value = prefs.getBoolean("biometric_enabled", false)
        }.onFailure { clearSession() }
    }

    fun saveSession(user: User, token: String) {
        runCatching {
            val encrypted = keystore.encrypt(keystoreConfig, token)
            prefs.edit()
                .putString("token_ct",  encrypted.ciphertext)
                .putString("token_iv",  encrypted.iv)
                .putString("user_json", Json.encodeToString(UserDto.fromDomain(user)))
                .apply()
            _token.value = token
            _user.value  = user
        }
    }

    // ── Biometric credential storage ──────────────────────────────────────────

    /**
     * Persists the user's email + password encrypted under a dedicated AES key
     * so biometric login can replay a real API call transparently.
     */
    fun saveBiometricCredentials(email: String, password: String) {
        runCatching {
            val encEmail    = keystore.encrypt(credConfig, email)
            val encPassword = keystore.encrypt(credConfig, password)
            prefs.edit()
                .putString("bio_email_ct",    encEmail.ciphertext)
                .putString("bio_email_iv",    encEmail.iv)
                .putString("bio_password_ct", encPassword.ciphertext)
                .putString("bio_password_iv", encPassword.iv)
                .apply()
        }
    }

    fun getBiometricCredentials(): Pair<String, String>? = runCatching {
        val emailCt    = prefs.getString("bio_email_ct",    null) ?: return null
        val emailIv    = prefs.getString("bio_email_iv",    null) ?: return null
        val passwordCt = prefs.getString("bio_password_ct", null) ?: return null
        val passwordIv = prefs.getString("bio_password_iv", null) ?: return null
        val email    = keystore.decrypt(credConfig, EncryptedData(emailCt,    emailIv))    ?: return null
        val password = keystore.decrypt(credConfig, EncryptedData(passwordCt, passwordIv)) ?: return null
        email to password
    }.getOrNull()

    fun hasBiometricCredentials(): Boolean =
        prefs.getString("bio_email_ct", null) != null

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
        _biometricEnabled.value = enabled
        if (!enabled) clearBiometricCredentials()
    }

    private fun clearBiometricCredentials() {
        prefs.edit()
            .remove("bio_email_ct").remove("bio_email_iv")
            .remove("bio_password_ct").remove("bio_password_iv")
            .apply()
    }

    // ── Session lifecycle ─────────────────────────────────────────────────────

    fun clearSession() {
        prefs.edit()
            .remove("token_ct").remove("token_iv").remove("user_json")
            .apply()
        _token.value = null
        _user.value  = null
        // Note: biometric_enabled + credentials are intentionally preserved so
        // the user can re-authenticate biometrically after a session expiry.
    }

    fun isAuthenticated(): Boolean = _token.value != null
    fun isBiometricEnabled(): Boolean = _biometricEnabled.value
    fun currentUser(): User? = _user.value
    fun currentToken(): String? = _token.value
}