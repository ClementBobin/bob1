package org.bob1.capacity.permission

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import dev.kindling.android.natif.PermissionHelper
import dev.kindling.android.natif.PermissionRequest
import dev.kindling.android.natif.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * POST_NOTIFICATIONS (Android 13+). Call [register] from `Activity.onCreate` (before STARTED) and [release] from `onDestroy`
 * so the singleton never keeps the Activity alive.
 */
class NotificationPermission(context: Context) {
    private val helper = PermissionHelper(context)
    private var launcher: ActivityResultLauncher<Array<String>>? = null

    private val _granted = MutableStateFlow(helper.isGranted(PermissionRequest.Notifications))
    val granted: StateFlow<Boolean> = _granted.asStateFlow()

    fun register(activity: FragmentActivity) {
        _granted.value = isGranted()
        launcher = helper.registerLauncher(activity) { results ->
            _granted.value = results.values.all { it is PermissionStatus.Granted }
        }
    }

    fun release() { launcher = null }

    fun isGranted(): Boolean = helper.isGranted(PermissionRequest.Notifications)

    /** Shows the system dialog when needed; does nothing if already granted. */
    fun request() {
        val l = launcher ?: return
        helper.request(PermissionRequest.Notifications, l) { _granted.value = true }
    }
}
