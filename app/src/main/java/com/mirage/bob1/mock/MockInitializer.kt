package com.mirage.bob1.mock

import android.util.Log
import com.mirage.bob1.mock.handlers.*
import com.mirage.bob1.mock.registry.MockRegistry

object MockInitializer {

    private const val TAG = "MockRegistry"

    fun init(debug: Boolean = false) {
        MockRegistry.clear()

        MockRegistry.registerMany(
            authHandlers
            + divisionHandlers
            + locationHandlers
            + matchHandlers
            + notificationHandlers
            + pointRuleHandlers
            + teamHandlers
        )

        if (debug) {
            Log.d(TAG, "=== Registered mock routes ===")
            MockRegistry.listRoutes().forEach { Log.d(TAG, it) }
        }
    }
}
