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
 * encryption. Both the session JWT and the biometric token are encrypted before
 * being stored in SharedPreferences — keys never leave Android Keystore hardware.
 *
 * ## Biometric flow (no credentials stored)
 * After a successful password login the server issues a dedicated biometric token
 * via GET /api/auth/generate-biometric-token. That token is stored here encrypted.
 * On the next launch the app shows the system biometric prompt; on success it
 * calls POST /api/auth/biometric-login with the stored token to obtain a fresh JWT.
 * Credentials (email/password) are never persisted on the device.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bob1_secure_prefs", Context.MODE_PRIVATE)

    private val keystore       = KeystoreHelper()
    private val sessionConfig  = KeystoreConfig.default("bob1_session_key")
    private val bioTokenConfig = KeystoreConfig.default("bob1_bio_token_key")

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
            val token = keystore.decrypt(sessionConfig, EncryptedData(encCiphertext, encIv))
            val userJson = prefs.getString("user_json", null)
            val user = userJson?.let { Json.decodeFromString<UserDto>(it).toDomain() }
            _token.value            = token
            _user.value             = user
            _biometricEnabled.value = prefs.getBoolean("biometric_enabled", false)
        }.onFailure { clearSession() }
    }

    // ── Session JWT ───────────────────────────────────────────────────────────

    fun saveSession(user: User, token: String) {
        runCatching {
            val encrypted = keystore.encrypt(sessionConfig, token)
            prefs.edit()
                .putString("token_ct",  encrypted.ciphertext)
                .putString("token_iv",  encrypted.iv)
                .putString("user_json", Json.encodeToString(UserDto.fromDomain(user)))
                .apply()
            _token.value = token
            _user.value  = user
        }
    }

    // ── Biometric token (server-issued, not credentials) ──────────────────────

    /**
     * Stores the server-issued biometric token encrypted with AES-256-GCM.
     * This token is used at the next launch to obtain a fresh session JWT
     * from POST /api/auth/biometric-login — no email/password needed.
     */
    fun saveBiometricToken(bioToken: String) {
        runCatching {
            val encrypted = keystore.encrypt(bioTokenConfig, bioToken)
            prefs.edit()
                .putString("bio_token_ct", encrypted.ciphertext)
                .putString("bio_token_iv", encrypted.iv)
                .apply()
        }
    }

    fun getBiometricToken(): String? = runCatching {
        val ct = prefs.getString("bio_token_ct", null) ?: return null
        val iv = prefs.getString("bio_token_iv", null) ?: return null
        keystore.decrypt(bioTokenConfig, EncryptedData(ct, iv))
    }.getOrNull()

    fun hasBiometricToken(): Boolean =
        prefs.getString("bio_token_ct", null) != null

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
        _biometricEnabled.value = enabled
        if (!enabled) clearBiometricToken()
    }

    fun clearBiometricToken() {
        prefs.edit().remove("bio_token_ct").remove("bio_token_iv").apply()
    }

    // ── Session lifecycle ─────────────────────────────────────────────────────

    fun clearSession() {
        prefs.edit()
            .remove("token_ct").remove("token_iv").remove("user_json")
            .apply()
        _token.value = null
        _user.value  = null
        // biometric_enabled + token are preserved so the user can re-authenticate
        // biometrically after a session expiry without re-entering their password.
    }

    fun isAuthenticated(): Boolean = _token.value != null
    fun isBiometricEnabled(): Boolean = _biometricEnabled.value
    fun currentUser(): User? = _user.value
    fun currentToken(): String? = _token.value
}