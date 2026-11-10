package com.mirage.bob1.data.remote

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Rewrites every request's base URL (scheme/host/port/path) to the current value
 * of [ApiUrlHolder.current], which may come from Firebase Remote Config.
 *
 * Retrofit is built against a placeholder base URL; this interceptor is what
 * actually determines where requests go, allowing the URL to change at runtime.
 */
class RemoteConfigBaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val base = ApiUrlHolder.current.toHttpUrlOrNull() ?: return chain.proceed(request)

        val basePath = base.encodedPath
        val requestPath = request.url.encodedPath
        val combinedPath = basePath.trimEnd('/') + "/" + requestPath.trimStart('/')

        val newUrl = request.url.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .encodedPath(combinedPath)
            .build()

        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}