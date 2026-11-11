package com.mirage.bob1.ui.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.ui.core.AppMode
import dev.kindling.compose.KDestination

data class TabSpec(val destination: KDestination, val label: String, val icon: ImageVector)

private val CalendarTab = TabSpec(CalendarDestination, "Calendrier", Icons.Outlined.CalendarMonth)

private val CommitmentsTab = TabSpec(CommitmentsDestination, "Engagements", Icons.Outlined.VerifiedUser)

private val RankingTab = TabSpec(RankingDestination, "Classement", Icons.Outlined.EmojiEvents)

private val ProfileTab = TabSpec(ProfileDestination, "Profil", Icons.Outlined.Person)

private val StaffCalendarTab = TabSpec(StaffCalendarDestination, "Calendrier", Icons.Outlined.CalendarMonth)

private val StaffAssignmentsTab = TabSpec(StaffAssignmentsDestination, "Affectations", Icons.Outlined.Shield)

private val StaffRankingTab = TabSpec(StaffRankingDestination, "Classement", Icons.Outlined.EmojiEvents)

/** Bottom bar content for a user: Parent or Staff experience (switchable when both roles). Administration lives on the web. */
fun tabsFor(roles: Set<UserRole>, mode: AppMode): List<TabSpec> {
    val parent = UserRole.Parent in roles
    val staff = UserRole.Staff in roles
    val base = when {
        parent && (mode == AppMode.Parent || !staff) -> listOf(CalendarTab, CommitmentsTab, RankingTab, ProfileTab)
        staff -> listOf(StaffCalendarTab, StaffAssignmentsTab, StaffRankingTab, ProfileTab)
        else -> listOf(ProfileTab)
    }
    return base
}
