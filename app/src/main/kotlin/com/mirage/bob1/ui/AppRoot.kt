package com.mirage.bob1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mirage.bob1.ui.components.LoadingBox
import com.mirage.bob1.ui.core.theme.Bob1Theme
import com.mirage.bob1.ui.navigation.AuthNavGraph
import com.mirage.bob1.ui.navigation.MainNavGraph
import dev.kindling.core.components.ui.toast.KToaster

@Composable
fun AppRoot(viewModel: RootViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AppRootContent(state)
}

@Composable
fun AppRootContent(state: RootState) {
    Bob1Theme {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (state.authenticated) {
                null -> LoadingBox()
                // key(): signing in / out rebuilds the NavHost so the back stack never leaks across sessions.
                false -> key("auth") { AuthNavGraph() }
                true -> state.roles?.let { roles -> key("main", state.mode) { MainNavGraph(roles, state.mode) } } ?: LoadingBox()
            }
            KToaster()
        }
    }
}
