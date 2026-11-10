package org.bob1.capacity.session

import android.content.Context
import dev.kindling.android.storage.KEncryptedTokenStore
import dev.kindling.android.storage.KSessionManager
import kotlinx.coroutines.flow.StateFlow

/**
 * JWT access/refresh tokens, encrypted at rest (AES-GCM, Android Keystore).
 * Thin facade over Kindling's [KEncryptedTokenStore] + [KSessionManager] so the app never touches Kindling storage directly.
 */
class SecureSession(context: Context) {
    private val manager = KSessionManager(KEncryptedTokenStore(context, prefsName = "bob1_secure_session"))

    /** Hot value, `null` = signed out. Only meaningful once [isReady] is true. */
    val accessTokenFlow: StateFlow<String?> get() = manager.accessToken
    val isReady: StateFlow<Boolean> get() = manager.isReady

    /** Non-suspending read for the OkHttp interceptor. */
    val accessToken: String? get() = manager.accessToken.value
    val refreshToken: String? get() = manager.refreshToken.value

    suspend fun awaitReady() = manager.awaitReady()
    suspend fun save(accessToken: String, refreshToken: String) = manager.saveTokens(accessToken, refreshToken)
    suspend fun clear() = manager.clearSession()
}
