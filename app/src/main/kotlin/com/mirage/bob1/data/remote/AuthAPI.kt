package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.AuthResponseDto
import com.mirage.bob1.data.dto.LoginRequestDto
import com.mirage.bob1.data.dto.RefreshRequestDto
import com.mirage.bob1.data.dto.RegisterRequestDto
import com.mirage.bob1.di.AnonymousClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import javax.inject.Inject

/** Anonymous endpoints: this client has no session, and is also what refreshes the token of the authenticated one. */
internal class AuthAPI @Inject constructor(@AnonymousClient private val client: HttpClient) {

    suspend fun login(email: String, password: String): AuthResponseDto =
        client.post("auth/login") { setBodyJson(LoginRequestDto(email, password)) }.body()

    suspend fun register(request: RegisterRequestDto): AuthResponseDto =
        client.post("auth/register") { setBodyJson(request) }.body()

    suspend fun refresh(refreshToken: String): AuthResponseDto =
        client.post("auth/refresh") { setBodyJson(RefreshRequestDto(refreshToken)) }.body()
}
