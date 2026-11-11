package com.mirage.bob1

import android.app.Application
import com.mirage.bob1.data.remote.ApiUrlHolder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.mirage.bob1.domain.usecase.RefreshNotificationsUseCase
import com.mirage.bob1.domain.usecase.RegisterPushTokenUseCase
import org.bob1.capacity.push.PushBridge
import org.bob1.capacity.push.PushChannels
import org.bob1.capacity.config.RemoteConfig

@HiltAndroidApp
class Bob1Application : Application() {

    @Inject lateinit var registerPushToken: RegisterPushTokenUseCase
    @Inject lateinit var refreshNotifications: RefreshNotificationsUseCase
    @Inject lateinit var remoteConfig: RemoteConfig

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate() // Hilt injects the fields here
        PushChannels.register(this)

        remoteConfig.configure(3600, mapOf("api_base_url" to BuildConfig.API_BASE_URL))
        remoteConfig.fetchAndActivate { ok ->
            if (ok) remoteConfig.getString("api_base_url").takeIf { it.isNotBlank() }?.let { ApiUrlHolder.current = it }
        }

        // The FCM service lives in :capacity and only talks to the app through this bridge.
        PushBridge.onToken = { token -> appScope.launch { registerPushToken(token) } }
        // Push only (no local notifications): while the app is open, just pull the list so the bell badge updates.
        PushBridge.onMessage = { appScope.launch { refreshNotifications() } }
    }
}
