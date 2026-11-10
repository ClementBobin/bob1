package org.bob1.app.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kindling.compose.KNavHost
import dev.kindling.core.components.ui.toast.KToaster
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.bob1.app.domain.model.UserRole
import org.bob1.app.domain.usecase.GetMeUseCase
import org.bob1.app.ui.core.AppMode
import org.bob1.app.ui.core.AppModeStore
import org.bob1.app.ui.core.components.tabsFor
import org.bob1.app.ui.screens.staff.*
import org.bob1.app.domain.usecase.ObserveSessionUseCase
import org.bob1.app.domain.usecase.RegisterPushTokenUseCase
import org.bob1.app.ui.core.Bob1ViewModel
import org.bob1.app.ui.core.components.Bob1BottomBar
import org.bob1.app.ui.core.components.LoadingBox
import org.bob1.app.ui.core.navigation.*
import org.bob1.app.ui.core.theme.Bob1Theme
import org.bob1.app.ui.screens.calendar.CalendarScreen
import org.bob1.app.ui.screens.commitments.CommitmentsScreen
import org.bob1.app.ui.screens.login.LoginScreen
import org.bob1.app.ui.screens.match.MatchScreen
import org.bob1.app.ui.screens.notifications.NotificationsScreen
import org.bob1.app.ui.screens.profile.ProfileScreen
import org.bob1.app.ui.screens.register.RegisterScreen
import org.bob1.app.ui.screens.ranking.RankingScreen
import org.bob1.capacity.permission.NotificationPermission

/** `authenticated`: null while the encrypted session is being read. */
data class RootState(val authenticated: Boolean? = null, val roles: Set<UserRole>? = null, val mode: AppMode = AppMode.Parent)

@HiltViewModel
class RootViewModel @Inject constructor(
    application: Application,
    observeSession: ObserveSessionUseCase,
    private val registerPushToken: RegisterPushTokenUseCase,
    private val notificationPermission: NotificationPermission,
    private val getMe: GetMeUseCase,
    private val modeStore: AppModeStore,
) : Bob1ViewModel<RootState>(RootState(), application) {

    init {
        viewModelScope.launch { modeStore.mode.collect { m -> updateState { copy(mode = m) } } }
        collectData({ observeSession() }) {
            onSuccess { authed ->
                updateState { copy(authenticated = authed, roles = if (authed == true) roles else null) }
                if (authed == true) {
                    loadRoles()
                    call({ registerPushToken() }, onError = {})   // a fresh login or a restored session: make sure the API knows this device
                    notificationPermission.request()
                }
            }
        }
    }

    /** Roles decide which tabs exist. If the profile can't be loaded (offline), fall back to the parent experience. */
    private fun loadRoles() = call(
        block = { getMe() },
        onError = { updateState { copy(roles = setOf(UserRole.Parent)) } },
    ) { me ->
        val roles = me.roles.ifEmpty { setOf(UserRole.Parent) }
        modeStore.set(if (UserRole.Parent in roles) AppMode.Parent else AppMode.Staff)
        updateState { copy(roles = roles) }
    }
}

@Composable
fun AppRoot(viewModel: RootViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Bob1Theme {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            when (state.authenticated) {
                null -> LoadingBox()
                // key(): signing in / out rebuilds the NavHost so the back stack never leaks across sessions.
                false -> key("auth") { AuthGraph() }
                true -> state.roles?.let { roles -> key("main", state.mode) { MainGraph(roles, state.mode) } } ?: LoadingBox()
            }
            KToaster()
        }
    }
}

@Composable
private fun AuthGraph() {
    val nav = rememberNavController()
    KNavHost(nav, startDestination = LoginDestination, modifier = Modifier.fillMaxSize()) {
        composable(LoginDestination.route) { LoginScreen(nav) }
        composable(RegisterDestination.route) { RegisterScreen(nav) }
    }
}

@Composable
private fun MainGraph(roles: Set<UserRole>, mode: AppMode) {
    val nav: NavHostController = rememberNavController()
    val tabs = tabsFor(roles, mode)
    val route = nav.currentBackStackEntryAsState().value?.destination?.route
    val showBar = route in tabs.map { it.destination.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBar) Bob1BottomBar(tabs, route) { dest ->
                nav.navigate(dest.route) {
                    popUpTo(tabs.first().destination.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        },
    ) { inner: PaddingValues ->
        KNavHost(
            nav, startDestination = tabs.first().destination,
            modifier = Modifier.padding(inner).let { if (showBar) it else it.navigationBarsPadding() },
        ) {
            // Parent
            composable(CalendarDestination.route) { CalendarScreen(nav) }
            composable(CommitmentsDestination.route) { CommitmentsScreen(nav) }
            composable(RankingDestination.route) { RankingScreen(nav) }
            composable(MatchDestination.PATTERN, arguments = matchArgs) { MatchScreen(nav) }
            // Staff
            composable(StaffCalendarDestination.route) { StaffCalendarScreen(nav) }
            composable(StaffAssignmentsDestination.route) { StaffAssignmentsScreen(nav) }
            composable(StaffRankingDestination.route) { StaffRankingScreen(nav) }
            composable(StaffMatchDestination.PATTERN, arguments = matchArgs) { StaffMatchScreen(nav) }
            // Shared
            composable(ProfileDestination.route) { ProfileScreen(nav) }
            composable(NotificationsDestination.route) { NotificationsScreen(nav) }
        }
    }
}

private val matchArgs = listOf(navArgument(MatchDestination.ARG) { type = NavType.StringType })
