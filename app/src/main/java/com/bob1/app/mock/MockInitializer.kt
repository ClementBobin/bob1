package com.bob1.app.mock

import android.util.Log
import com.bob1.app.mock.handlers.*
import com.bob1.app.mock.registry.MockRegistry

/**
 * Registers all mock handlers into [MockRegistry].
 *
 * Call this **once** during app startup — before any Ktor request is made.
 * In production builds this object should never be referenced.
 *
 * Usage in Application.onCreate:
 * ```kotlin
 * if (BuildConfig.MOCK_API) {
 *     MockInitializer.init(debug = BuildConfig.DEBUG)
 * }
 * ```
 */
object MockInitializer {

    private const val TAG = "MockRegistry"

    fun init(debug: Boolean = false) {
        MockRegistry.clear()

        MockRegistry.registerMany(
            authHandlers          // login, register, logout, me,
                    // forgot-password, reset-password, confirm-email
        )

        if (debug) {
            Log.d(TAG, "=== Registered mock routes ===")
            MockRegistry.listRoutes().forEach { Log.d(TAG, it) }
        }
    }
}