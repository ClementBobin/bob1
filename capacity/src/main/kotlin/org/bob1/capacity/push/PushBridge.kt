package org.bob1.capacity.push

/**
 * The FCM service lives in this module but must not know the app. The app plugs its callbacks in
 * `Application.onCreate()`; the service just forwards.
 */
object PushBridge {
    @Volatile var onToken: ((String) -> Unit)? = null

    /**
     * A push arrived while the app is in the foreground. Nothing is displayed by the system in that case
     * (and we do not post local notifications): the app should refresh its in-app notification list instead.
     */
    @Volatile var onMessage: ((data: Map<String, String>) -> Unit)? = null
}
