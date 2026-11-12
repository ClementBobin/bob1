package org.bob1.capacity.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject

/**
 * Thin wrapper around Firebase Remote Config, mirroring [org.bob1.capacity.push.PushMessaging]:
 * keeps Firebase details inside :capacity and degrades gracefully when Firebase is not
 * configured (no google-services.json).
 */
class RemoteConfig @Inject constructor() {

    fun isAvailable(): Boolean = runCatching { FirebaseRemoteConfig.getInstance() }.isSuccess

    fun configure(minimumFetchIntervalInSeconds: Long, defaults: Map<String, Any>) {
        val rc = FirebaseRemoteConfig.getInstance()
        rc.setConfigSettingsAsync(
            remoteConfigSettings { this.minimumFetchIntervalInSeconds = minimumFetchIntervalInSeconds }
        )
        rc.setDefaultsAsync(defaults)
    }

    fun fetchAndActivate(onComplete: (Boolean) -> Unit) {
        runCatching { FirebaseRemoteConfig.getInstance() }
            .onSuccess { rc -> rc.fetchAndActivate().addOnCompleteListener { task -> onComplete(task.isSuccessful) } }
            .onFailure { onComplete(false) }
    }

    fun getString(key: String): String =
        runCatching { FirebaseRemoteConfig.getInstance().getString(key) }.getOrDefault("")
}