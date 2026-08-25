package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.ErrorResponseDto
import com.mirage.bob1.data.local.SessionManager
import dev.kindling.android.natif.VibrationHelper
import dev.kindling.core.components.ui.toast.KToastManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Crée et configure le client HTTP Ktor partagé par toutes les API.
 *
 * - Ajoute [baseUrl] comme préfixe de toutes les requêtes.
 * - Injecte `Authorization: Bearer <token>` sur chaque requête via [SessionManager].
 * - Différencie les 401 sur `/auth/login` ou `/auth/register` (identifiants invalides)
 *   des 401 sur les routes protégées (session expirée → [SessionManager.clearSession]).
 */
fun createHttpClient(
    baseUrl: String,
    engine: HttpClientEngine = CIO.create(),
    vibrationHelper: VibrationHelper? = null,
    sessionManager: SessionManager? = null,
): HttpClient = HttpClient(engine) {

    defaultRequest {
        url(baseUrl)
        // Inject Bearer token on every request if a session exists
        sessionManager?.currentToken()?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues  = true
        })
    }

    install(HttpTimeout) {
        connectTimeoutMillis = 15_000
        socketTimeoutMillis  = 15_000
        requestTimeoutMillis = 15_000
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) { println("HTTP Client: $message") }
        }
        level = LogLevel.ALL
    }

    install(HttpCallValidator) {
        validateResponse { response ->
            val status = response.status.value
            when {
                status in 200..299 -> Unit

                status == 401 -> {
                    val path = response.call.request.url.encodedPath
                    val isAuthEndpoint = path.endsWith("/auth/login") ||
                        path.endsWith("/auth/register")
                    if (isAuthEndpoint) {
                        val msg = runCatching { response.body<ErrorResponseDto>().text }
                            .recoverCatching { response.bodyAsText().take(200) }
                            .getOrDefault("Identifiants invalides.")
                        vibrationHelper?.warning()
                        KToastManager.warning("Connexion échouée", msg)
                        throw HttpException.ClientError(status, msg)
                    } else {
                        sessionManager?.clearSession()
                        vibrationHelper?.warning()
                        KToastManager.warning("Session expirée", "Veuillez vous reconnecter.")
                        throw HttpException.ClientError(status, "Session expirée")
                    }
                }

                status in 400..499 -> {
                    val msg = runCatching { response.body<ErrorResponseDto>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    vibrationHelper?.warning()
                    KToastManager.warning("Client error ($status)", msg)
                    throw HttpException.ClientError(status, msg)
                }

                status in 500..599 -> {
                    val msg = runCatching { response.body<ErrorResponseDto>().text }
                        .recoverCatching { response.bodyAsText().take(200) }
                        .getOrDefault("No details provided")
                    vibrationHelper?.error()
                    KToastManager.error("Server error ($status)", msg)
                    throw HttpException.ServerError(status, msg)
                }
            }
        }

        handleResponseExceptionWithRequest { exception, _ ->
            if (exception is HttpException) return@handleResponseExceptionWithRequest
            vibrationHelper?.error()
            KToastManager.error("Network error", exception.message ?: "No details provided")
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

fun HttpResponse.accept(vararg codes: HttpStatusCode) = apply {
    if (status !in codes) {
        val message     = "Unexpected status: HTTP $status"
        val description = "Expected: ${codes.joinToString()}"
        KToastManager.warning(message, description)
        throw HttpException.NotAccepted("$message. $description")
    }
}

// ── Exceptions ────────────────────────────────────────────────────────────────

sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted(message: String)                      : HttpException(message)
    class ClientError(val statusCode: Int, message: String) : HttpException(message)
    class ServerError(val statusCode: Int, message: String) : HttpException(message)
}
