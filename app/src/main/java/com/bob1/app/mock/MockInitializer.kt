package com.bob1.app.mock

import android.util.Log
import com.bob1.app.mock.handlers.*
import com.bob1.app.mock.registry.MockRegistry

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
