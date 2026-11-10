package com.mirage.bob1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.mirage.bob1.BuildConfig
import com.mirage.bob1.domain.usecase.RefreshNotificationsUseCase
import com.mirage.bob1.domain.usecase.RegisterPushTokenUseCase
import org.bob1.capacity.push.PushBridge
import org.bob1.capacity.push.PushChannels
import com.mirage.bob1.data.remote.ApiUrlHolder
import org.bob1.capacity.config.RemoteConfig

@HiltAndroidApp
class Bob1Application : Application() {

    @Inject lateinit var registerPushToken: RegisterPushTokenUseCase
    @Inject lateinit var refreshNotifications: RefreshNotificationsUseCase

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate() // Hilt injects the fields here
        PushChannels.register(this)

        // --- Firebase Remote Config for the API URL ---
        // Wrapped in :capacity so Firebase stays out of :app. Degrades gracefully
        // when Firebase is not configured (no google-services.json).
        val remoteConfig = RemoteConfig()
        if (remoteConfig.isAvailable()) {
            remoteConfig.configure(
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600,
                defaults = mapOf("api_base_url" to ApiUrlHolder.current)
            )
            remoteConfig.fetchAndActivate { success ->
                if (success) {
                    val fetched = remoteConfig.getString("api_base_url")
                    if (fetched.isNotBlank()) ApiUrlHolder.current = fetched
                }
            }
        }

        // The FCM service lives in :capacity and only talks to the app through this bridge.
        PushBridge.onToken = { token -> appScope.launch { registerPushToken(token) } }
        // Push only (no local notifications): while the app is open, just pull the list so the bell badge updates.
        PushBridge.onMessage = { appScope.launch { refreshNotifications() } }
    }
}
