package com.bob1.app

import android.app.Application
import com.bob1.app.data.local.SessionManager
import com.bob1.app.di.appModule
import com.bob1.app.mock.MockInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Application : Application() {

    /**
     * Kill the session when the user leaves the app (all UI gone from memory).
     * TRIM_MEMORY_UI_HIDDEN fires when the last activity goes to background and
     * the app is no longer visible — equivalent to "app closed" from the user's
     * perspective. The biometric token is preserved so they can re-auth quickly
     * via POST /api/auth/biometric-login on next launch.
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_UI_HIDDEN) {
            GlobalContext.getOrNull()
                ?.getOrNull<SessionManager>()
                ?.clearSession()
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.MOCK_API) {
            MockInitializer.init(debug = BuildConfig.DEBUG)
        }

        startKoin {
            androidLogger(level = Level.ERROR)
            androidContext(this@Application)
            modules(appModule)
        }
    }
}