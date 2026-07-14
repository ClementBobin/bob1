package com.bob1.app.data.remote

import com.bob1.app.data.dto.*
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
}
