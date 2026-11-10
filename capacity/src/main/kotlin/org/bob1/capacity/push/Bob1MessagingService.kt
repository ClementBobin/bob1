package org.bob1.capacity.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Bob1MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        PushBridge.onToken?.invoke(token)
    }

    // Background / killed app: FCM shows the notification itself (the API sends a notification payload), this isn't called.
    override fun onMessageReceived(message: RemoteMessage) {
        PushBridge.onMessage?.invoke(message.data)
    }
}
