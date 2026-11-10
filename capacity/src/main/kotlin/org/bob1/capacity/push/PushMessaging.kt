package org.bob1.capacity.push

import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class PushMessaging {
    /**
     * Current FCM token, or `null` when Firebase is not configured (no google-services.json) or unavailable.
     * The app registers it on the API with `POST /me/push-tokens`.
     */
    suspend fun currentToken(): String? =
        runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
    addOnCanceledListener { cont.cancel() }
}
