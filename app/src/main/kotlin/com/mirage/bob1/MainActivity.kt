package com.mirage.bob1ob1

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.mirage.bob1ob1.ui.AppRoot
import org.bob1.capacity.permission.NotificationPermission

// FragmentActivity (not ComponentActivity) because Kindling's PermissionHelper registers its launcher on one.
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var notificationPermission: NotificationPermission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        notificationPermission.register(this) // must happen before STARTED
        setContent { AppRoot() }
    }

    override fun onDestroy() {
        notificationPermission.release()
        super.onDestroy()
    }
}
