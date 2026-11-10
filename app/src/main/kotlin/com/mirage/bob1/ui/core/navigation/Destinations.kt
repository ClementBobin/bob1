package com.mirage.bob1.ui.core.navigation

import androidx.navigation.NavOptions
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dev.kindling.compose.KDestination
import dev.kindling.compose.NavigationEvent

data object LoginDestination : KDestination { override val route = "login" }
data object RegisterDestination : KDestination { override val route = "register" }
data object CalendarDestination : KDestination { override val route = "calendar" }
data object CommitmentsDestination : KDestination { override val route = "commitments" }
data object RankingDestination : KDestination { override val route = "ranking" }
data object ProfileDestination : KDestination { override val route = "profile" }
data object NotificationsDestination : KDestination { override val route = "notifications" }

data class MatchDestination(val matchId: String) : KDestination {
    override val route get() = "match/$matchId"
    override val arguments: List<NamedNavArgument> = listOf(navArgument(ARG) { type = NavType.StringType })

    companion object {
        const val ARG = "matchId"
        const val PATTERN = "match/{$ARG}"
    }
}

// ── Staff ──
data object StaffCalendarDestination : KDestination { override val route = "staff" }
data object StaffAssignmentsDestination : KDestination { override val route = "staff/assignments" }
data object StaffRankingDestination : KDestination { override val route = "staff/ranking" }

data class StaffMatchDestination(val matchId: String) : KDestination {
    override val route get() = "staff/match/$matchId"
    companion object { const val PATTERN = "staff/match/{${MatchDestination.ARG}}" }
}

// ── One-shot events a ViewModel can send; KScreen handles NavigationEvent itself, the rest goes to rememberUiEventHandler ──
data class Navigate(override val destination: KDestination, override val navOptions: NavOptions? = null) : NavigationEvent
data class ShowMessage(val text: String, val isError: Boolean = true)
data class OpenDirections(val address: String)
data class ShareText(val text: String)
data object GoBack
data object HapticConfirm
data object HapticReject
