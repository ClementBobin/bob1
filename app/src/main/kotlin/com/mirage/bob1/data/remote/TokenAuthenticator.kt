package com.mirage.bob1.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.bob1.capacity.session.SecureSession
import retrofit2.HttpException

/** On a 401: refresh once (serialised), replay the call; if the refresh token is rejected, sign the user out. */
class TokenAuthenticator(private val session: SecureSession, private val authApi: AuthApi) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header(RETRY_HEADER) != null) return null // already replayed once

        synchronized(this) {
            val sent = response.request.header("Authorization")?.removePrefix("Bearer ")
            val current = session.accessToken
            // Another call refreshed while we were waiting for the lock.
            if (current != null && current != sent) return response.request.withToken(current)

            val refresh = session.refreshToken ?: return null
            return try {
                val fresh = runBlocking { authApi.refresh(com.mirage.bob1.data.dto.RefreshRequestDto(refresh)) }
                runBlocking { session.save(fresh.accessToken, fresh.refreshToken) }
                response.request.withToken(fresh.accessToken)
            } catch (e: HttpException) {
                // The server said no (revoked / expired refresh token): the session is over.
                if (e.code() in 400..499) runBlocking { session.clear() }
                null
            } catch (e: Exception) {
                null // offline etc.: keep the session, the call just fails
            }
        }
    }

    private fun Request.withToken(token: String) =
        newBuilder().header("Authorization", "Bearer $token").header(RETRY_HEADER, "1").build()

    private companion object { const val RETRY_HEADER = "X-Auth-Retry" }
}
