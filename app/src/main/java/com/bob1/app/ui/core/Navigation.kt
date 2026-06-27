package com.bob1.app.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.bob1.app.data.local.SessionManager
import com.bob1.app.ui.screens.admin.AdminScreen
import com.bob1.app.ui.screens.calendar.CalendarScreen
import com.bob1.app.ui.screens.notifications.NotificationsScreen
import com.bob1.app.ui.screens.profile.ProfileScreen
import dev.kindling.compose.KNavHost
import org.koin.compose.koinInject

// ── Destinations ──────────────────────────────────────────────────────────────

sealed class Destination(override val route: String) : dev.kindling.compose.Destination {
    object Login         : Destination("login")
    object Register      : Destination("register")
    object Calendar      : Destination("calendar")
    object Notifications : Destination("notifications")
    object Profile       : Destination("profile")
    object Admin         : Destination("admin")
}

// ── NavHost ───────────────────────────────────────────────────────────────────

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val session: SessionManager = koinInject()
    val token by session.token.collectAsState()

    KNavHost(
        navController    = navController,
        startDestination = if (token != null) Destination.Calendar else Destination.Login,
        modifier         = modifier,
    ) {
        composable(Destination.Calendar.route) {
            CalendarScreen(navController)
        }
        composable(Destination.Notifications.route) {
            NotificationsScreen(navController)
        }
        composable(Destination.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Destination.Admin.route) {
            AdminScreen(navController)
        }
    }
}