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
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("basketball_secure_prefs", Context.MODE_PRIVATE)

    private val keystore = KeystoreHelper()
    private val keystoreConfig = KeystoreConfig.default("basketball_session_key")

    private val _user  = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    init { restoreSession() }

    private fun restoreSession() {
        runCatching {
            val encCiphertext = prefs.getString("token_ct", null) ?: return
            val encIv         = prefs.getString("token_iv", null) ?: return
            val token = keystore.decrypt(keystoreConfig, EncryptedData(encCiphertext, encIv))
            val userJson = prefs.getString("user_json", null)
            val user = userJson?.let { Json.decodeFromString<UserDto>(it).toDomain() }
            _token.value = token
            _user.value  = user
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

    fun clearSession() {
        prefs.edit().remove("token_ct").remove("token_iv").remove("user_json").apply()
        _token.value = null
        _user.value  = null
    }

    fun isAuthenticated(): Boolean = _token.value != null
    fun currentUser(): User? = _user.value
    fun currentToken(): String? = _token.value
}