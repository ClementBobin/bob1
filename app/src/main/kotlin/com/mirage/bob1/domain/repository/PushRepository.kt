package com.mirage.bob1.domain.repository

interface PushRepository {
    /** Fetches the FCM token and registers it for the signed-in user (no-op if Firebase isn't configured). */
    suspend fun registerCurrentToken(): Result<Unit>
    suspend fun registerToken(token: String): Result<Unit>
}
