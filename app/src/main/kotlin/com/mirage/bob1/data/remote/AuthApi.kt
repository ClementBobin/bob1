package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.*
import retrofit2.http.Body
import retrofit2.http.POST

/** Anonymous endpoints: no interceptor, no authenticator (it is what the authenticator itself calls to refresh). */
interface AuthApi {
    @POST("auth/register") suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto
    @POST("auth/login") suspend fun login(@Body body: LoginRequestDto): AuthResponseDto
    @POST("auth/refresh") suspend fun refresh(@Body body: RefreshRequestDto): AuthResponseDto
}
