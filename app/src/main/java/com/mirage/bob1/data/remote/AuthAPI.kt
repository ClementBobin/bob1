package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class AuthAPI(private val client: HttpClient) {

    suspend fun login(request: LoginRequestDto): LoginResponseDto =
        client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun register(request: RegisterRequestDto): UserDto =
        client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun logout(): HttpResponse =
        client.post("/api/auth/logout")

    suspend fun getCurrentUser(): UserDto =
        client.get("/api/auth/me").body()

    /**
     * Requests a long-lived biometric token from the server (requires a valid
     * session JWT). The returned token is stored encrypted on-device and later
     * replayed at [biometricLogin] — credentials never touch the client again.
     */
    suspend fun generateBiometricToken(): LoginResponseDto =
        client.get("/api/auth/generate-biometric-token").body()

    /**
     * Exchanges a previously issued biometric token for a fresh session JWT.
     * Called after the local hardware biometric prompt succeeds.
     * The token is sent via the X-Bio-Token header instead of the request body.
     */
    suspend fun biometricLogin(bioToken: String): LoginResponseDto =
        client.post("/api/auth/biometric-login") {
            header("X-Bio-Token", bioToken)
        }.body()

    /** Revokes the biometric token server-side (called on disable or logout). */
    suspend fun removeBiometricToken(): HttpResponse =
        client.post("/api/auth/biometric-remove")
}