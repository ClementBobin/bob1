package com.mirage.bob1ob1ob1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.mirage.bob1ob1ob1.domain.usecase.RefreshNotificationsUseCase
import com.mirage.bob1ob1ob1.domain.usecase.RegisterPushTokenUseCase
import org.bob1.capacity.push.PushBridge
import org.bob1.capacity.push.PushChannels

@HiltAndroidApp
class Bob1Application : Application() {

    @Inject lateinit var registerPushToken: RegisterPushTokenUseCase
    @Inject lateinit var refreshNotifications: RefreshNotificationsUseCase

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate() // Hilt injects the fields here
        PushChannels.register(this)

        // The FCM service lives in :capacity and only talks to the app through this bridge.
        PushBridge.onToken = { token -> appScope.launch { registerPushToken(token) } }
        // Push only (no local notifications): while the app is open, just pull the list so the bell badge updates.
        PushBridge.onMessage = { appScope.launch { refreshNotifications() } }
    }
}
