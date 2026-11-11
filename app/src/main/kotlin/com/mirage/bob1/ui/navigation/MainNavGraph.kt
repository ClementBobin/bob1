package com.mirage.bob1.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.ui.components.Bob1BottomBar
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.core.navigation.CalendarDestination
import com.mirage.bob1.ui.core.navigation.CommitmentsDestination
import com.mirage.bob1.ui.core.navigation.MatchDestination
import com.mirage.bob1.ui.core.navigation.NotificationsDestination
import com.mirage.bob1.ui.core.navigation.ProfileDestination
import com.mirage.bob1.ui.core.navigation.RankingDestination
import com.mirage.bob1.ui.core.navigation.StaffAssignmentsDestination
import com.mirage.bob1.ui.core.navigation.StaffCalendarDestination
import com.mirage.bob1.ui.core.navigation.StaffMatchDestination
import com.mirage.bob1.ui.core.navigation.StaffRankingDestination
import com.mirage.bob1.ui.core.navigation.tabsFor
import com.mirage.bob1.ui.screens.calendar.CalendarScreen
import com.mirage.bob1.ui.screens.commitments.CommitmentsScreen
import com.mirage.bob1.ui.screens.match.MatchScreen
import com.mirage.bob1.ui.screens.notifications.NotificationsScreen
import com.mirage.bob1.ui.screens.profile.ProfileScreen
import com.mirage.bob1.ui.screens.ranking.RankingScreen
import com.mirage.bob1.ui.screens.staff.StaffAssignmentsScreen
import com.mirage.bob1.ui.screens.staff.StaffCalendarScreen
import com.mirage.bob1.ui.screens.staff.StaffMatchScreen
import com.mirage.bob1.ui.screens.staff.StaffRankingScreen
import dev.kindling.compose.KNavHost

private val matchArgs = listOf(navArgument(MatchDestination.ARG) { type = NavType.StringType })

@Composable
fun MainNavGraph(roles: Set<UserRole>, mode: AppMode) {
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
