package com.bob1.app.ui.core.components.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.bob1.app.ui.core.Destination

@Composable
fun AppBottomBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick  = { navController.navigate(Destination.Calendar.route) },
            icon     = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label    = { Text("Calendrier") },
        )
        NavigationBarItem(
            selected = false,
            onClick  = { navController.navigate(Destination.Notifications.route) },
            icon     = { Icon(Icons.Default.Notifications, contentDescription = null) },
            label    = { Text("Notifs") },
        )
        NavigationBarItem(
            selected = false,
            onClick  = { navController.navigate(Destination.Profile.route) },
            icon     = { Icon(Icons.Default.Person, contentDescription = null) },
            label    = { Text("Profil") },
        )
    }
}