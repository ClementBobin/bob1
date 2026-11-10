package com.mirage.bob1.data.remote

import com.mirage.bob1.BuildConfig

/**
 * Mutable holder for the API base URL.
 *
 * Initialized from the build-time [BuildConfig.API_BASE_URL] and updated when
 * Firebase Remote Config fetches a new value. Read by [RemoteConfigBaseUrlInterceptor].
 */
object ApiUrlHolder {
    @Volatile
    var current: String = BuildConfig.API_BASE_URL
}