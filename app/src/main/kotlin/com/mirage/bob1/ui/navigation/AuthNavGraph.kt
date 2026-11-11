package com.mirage.bob1.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mirage.bob1.ui.core.navigation.LoginDestination
import com.mirage.bob1.ui.core.navigation.RegisterDestination
import com.mirage.bob1.ui.screens.login.LoginScreen
import com.mirage.bob1.ui.screens.register.RegisterScreen
import dev.kindling.compose.KNavHost

@Composable
fun AuthNavGraph() {
    val nav = rememberNavController()
    KNavHost(nav, startDestination = LoginDestination, modifier = Modifier.fillMaxSize()) {
        composable(LoginDestination.route) { LoginScreen(nav) }
        composable(RegisterDestination.route) { RegisterScreen(nav) }
    }
}
