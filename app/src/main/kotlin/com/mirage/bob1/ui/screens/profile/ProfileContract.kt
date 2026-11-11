package com.mirage.bob1.ui.screens.profile

import com.mirage.bob1.domain.model.Family
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.ui.core.AppMode

data class ProfileState(
    val user: User? = null,
    val family: Family? = null,
    val notificationsEnabled: Boolean = true,
    val inviting: Boolean = false,
    val loggingOut: Boolean = false,
    val mode: AppMode = AppMode.Parent,
) {
    /** Only people who are both parent and staff get the switch. */
    val canSwitchMode get() = user?.roles?.let { UserRole.Parent in it && UserRole.Staff in it } == true
}

/** What [ProfileContent] can ask for; keeps the content stateless and previewable. */
data class ProfileActions(
    val enableNotifications: () -> Unit,
    val invite: () -> Unit,
    val logout: () -> Unit,
    val setMode: (AppMode) -> Unit,
)
