package com.mirage.bob1.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import org.bob1.capacity.session.SecureSession

class AuthInterceptor(private val session: SecureSession) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = session.accessToken
        if (token == null || request.header("Authorization") != null) return chain.proceed(request)
        return chain.proceed(request.newBuilder().header("Authorization", "Bearer $token").build())
    }
}
