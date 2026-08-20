package com.bob1.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.bob1.app.data.dto.UserDto
import com.bob1.app.domain.model.User
import dev.kindling.android.natif.KeystoreHelper
import dev.kindling.android.natif.KeystoreConfig
import dev.kindling.android.natif.EncryptedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class SessionManager(
    context: Context,
    private val fetchRemoteUser: suspend () -> Result<User>? = { null }
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bob1_secure_prefs", Context.MODE_PRIVATE)

    private val keystore      = KeystoreHelper()
    private val sessionConfig  = KeystoreConfig.default("bob1_session_key")
    private val bioTokenConfig = KeystoreConfig.default("bob1_bio_token_key")

    private val _user  = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    init { 
        restoreSession() 
    }

    private fun restoreSession() {
        runCatching {
            val encCiphertext = prefs.getString("token_ct", null) ?: return
            val encIv         = prefs.getString("token_iv", null) ?: return
            
            // Check token expiration time
            val expiresTime = prefs.getLong("token_expires_at", 0L)
            if (expiresTime > 0 && System.currentTimeMillis() >= expiresTime) {
                // Token is expired; clear session but preserve biometric flags if needed
                clearSession()
                return
            }

            val token = keystore.decrypt(sessionConfig, EncryptedData(encCiphertext, encIv))
            
            // Load cached user data for immediate UI rendering
            val userJson = prefs.getString("user_json", null)
            val cachedUser = userJson?.let { Json.decodeFromString<UserDto>(it).toDomain() }

            _token.value            = token
            _user.value             = cachedUser
            _biometricEnabled.value = prefs.getBoolean("biometric_enabled", false)

            // If token is valid, verify/refresh user data from API in the background
            if (token != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    runCatching {
                        fetchRemoteUser()?.onSuccess { freshUser ->
                            // Keep the existing expiresTime when updating the session profile
                            saveSession(freshUser, token, expiresTime)
                        }
                    }
                }
            }
        }.onFailure { clearSession() }
    }

    fun saveToken(token: String) {
        _token.value = token
    }

    fun saveSession(user: User, token: String, expiresTime: Long) {
        runCatching {
            val encrypted = keystore.encrypt(sessionConfig, token)
            prefs.edit()
                .putString("token_ct", encrypted.ciphertext)
                .putString("token_iv", encrypted.iv)
                .putLong("token_expires_at", expiresTime)
                .putString("user_json", Json.encodeToString(UserDto.fromDomain(user)))
                .apply()
            _token.value = token
            _user.value  = user
        }
    }

    // ── Biometric token methods (unchanged) ───────────────────────────────────

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
            .remove("token_ct")
            .remove("token_iv")
            .remove("token_expires_at")
            .apply()
        _token.value = null
        _user.value  = null
    }

    fun isAuthenticated(): Boolean = _token.value != null
    fun isBiometricEnabled(): Boolean = _biometricEnabled.value
    fun currentUser(): User? = _user.value
    fun currentToken(): String? = _token.value
}