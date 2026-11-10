package org.bob1.capacity.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/**
 * Android 8+ needs a channel for the notifications FCM displays. This only declares it; nothing is posted locally.
 * The same id is referenced by `default_notification_channel_id` in the app manifest.
 */
object PushChannels {
    const val CHANNEL_ID = "bob1_push"

    fun register(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Notifications du club", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Annonces, rappels de match et affectations"
            }
        )
    }
}
