package com.mirage.bob1.data.remote

import android.annotation.SuppressLint
import android.util.Log
import dev.kindling.android.natif.VibrationHelper
import dev.kindling.core.components.ui.toast.KToastManager
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.HttpClientCall
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import java.io.IOException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import com.mirage.bob1.data.dto.AuthResponseDto
import com.mirage.bob1.data.dto.ProblemDto
import org.bob1.capacity.session.SecureSession

/**
 * Builds the Ktor client shared by every API class.
 *
 * - [baseUrl] prefixes every request (it ends with a slash, request paths are relative: `"matches"`, not `"/matches"`).
 * - With a [sessionManager], `Authorization: Bearer <token>` is added to each request.
 * - A 401 on a protected route triggers ONE refresh through [refreshSession] (serialised) and a replay;
 *   if the refresh token is rejected the session is cleared, which sends the user back to the login screen.
 * - Any other failure becomes an [HttpException], with a toast and a vibration (like `KToastManager` / [VibrationHelper] elsewhere in the app).
 */
@SuppressLint("MissingPermission") // VIBRATE is declared in :capacity
fun createHttpClient(
    baseUrl: String,
    json: Json,
    engine: HttpClientEngine = CIO.create(),
    vibrationHelper: VibrationHelper? = null,
    sessionManager: SecureSession? = null,
    refreshSession: (suspend (refreshToken: String) -> AuthResponseDto)? = null,
    logLevel: LogLevel = LogLevel.NONE,
): HttpClient {
    val notifier = Notifier(vibrationHelper)
    val refreshLock = Mutex()

    val client = HttpClient(engine) {
        expectSuccess = false // we validate ourselves, below

        defaultRequest {
            url(baseUrl)
            sessionManager?.accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
        }
        install(ContentNegotiation) { json(json) }
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
            requestTimeoutMillis = 15_000
        }
        install(Logging) {
            logger = object : Logger { override fun log(message: String) { Log.d("Bob1Http", message) } }
            level = logLevel
            sanitizeHeader { it == HttpHeaders.Authorization }
        }
        install(HttpCallValidator) {
            // Connection problems (offline, DNS, timeout): tell the user once, hand a typed exception to the caller.
            handleResponseExceptionWithRequest { exception, _ ->
                if (exception is HttpException) return@handleResponseExceptionWithRequest
                val message = if (exception is IOException || exception.cause is IOException || exception::class.simpleName?.contains("Timeout") == true)
                    "Vous êtes hors-ligne. Réessayez dans un instant."
                else exception.message ?: "Erreur réseau."
                notifier.error("Erreur réseau", message)
                throw HttpException.NetworkError(message, exception)
            }
        }
    }

    // One interceptor does refresh-and-replay AND validation, so their order can never be a problem.
    client.plugin(HttpSend).intercept { request ->
        var call = execute(request)

        val path = request.url.encodedPath
        if (call.response.status == HttpStatusCode.Unauthorized && sessionManager != null && refreshSession != null && !path.isAuthEndpoint()) {
            val token = refreshLock.withLock {
                val sent = request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")
                val current = sessionManager.accessToken
                when {
                    current != null && current != sent -> current // someone else refreshed while we waited
                    else -> sessionManager.refreshToken?.let { rt ->
                        try {
                            refreshSession(rt).also { sessionManager.save(it.accessToken, it.refreshToken) }.accessToken
                        } catch (e: HttpException.ClientError) {
                            sessionManager.clear() // the server refused the refresh token: the session is over
                            null
                        } catch (e: Exception) {
                            null // offline etc.: keep the session, this call just fails
                        }
                    }
                }
            }
            if (token != null) {
                call = execute(HttpRequestBuilder().takeFrom(request).apply {
                    headers.remove(HttpHeaders.Authorization)
                    header(HttpHeaders.Authorization, "Bearer $token")
                })
            }
        }

        validate(call.response, json, notifier, sessionManager)
        call
    }
    return client
}

private fun String.isAuthEndpoint() = endsWith("/auth/login") || endsWith("/auth/register") || endsWith("/auth/refresh")

private suspend fun validate(response: HttpResponse, json: Json, notifier: Notifier, session: SecureSession?) {
    val status = response.status.value
    if (status in 200..299) return

    val path = response.call.request.url.encodedPath
    val raw = runCatching { response.bodyAsText() }.getOrDefault("")
    val problem = runCatching { json.decodeFromString<ProblemDto>(raw) }.getOrNull()
    val code = problem?.code
    val serverMessage = problem?.text ?: raw.take(200).ifBlank { "Aucun détail" }
    val message = friendlyMessage(status, code, serverMessage)

    when {
        status == 401 && path.endsWith("/auth/refresh") -> throw HttpException.ClientError(status, message, code) // silent: handled by the caller

        status == 401 && path.isAuthEndpoint() -> {
            notifier.warning("Connexion échouée", message)
            throw HttpException.ClientError(status, message, code)
        }

        status == 401 -> {
            session?.clear()
            notifier.warning("Session expirée", "Veuillez vous reconnecter.")
            throw HttpException.ClientError(status, "Session expirée, reconnectez-vous.", code)
        }

        status in 400..499 -> {
            notifier.warning("Erreur ($status)", message)
            throw HttpException.ClientError(status, message, code)
        }

        else -> {
            notifier.error("Erreur serveur ($status)", message)
            throw HttpException.ServerError(status, "Erreur serveur ($status). Réessayez plus tard.")
        }
    }
}

/** Toast + vibration; the same message is not repeated within a few seconds (a screen can fire several calls at once while offline). */
@SuppressLint("MissingPermission")
private class Notifier(private val vibration: VibrationHelper?) {
    private var last: Pair<String, Long> = "" to 0L

    private fun shouldShow(key: String): Boolean {
        val now = System.currentTimeMillis()
        if (last.first == key && now - last.second < 4_000) return false
        last = key to now
        return true
    }

    fun warning(title: String, message: String) {
        if (!shouldShow("$title|$message")) return
        runCatching { vibration?.warning() }
        KToastManager.warning(title, message)
    }

    fun error(title: String, message: String) {
        if (!shouldShow("$title|$message")) return
        runCatching { vibration?.error() }
        KToastManager.error(title, message)
    }
}
