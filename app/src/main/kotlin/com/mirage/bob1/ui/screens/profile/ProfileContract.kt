package com.mirage.bob1.ui.screens.profile

import com.mirage.bob1.domain.model.Family
import com.mirage.bob1.domain.model.Invitation
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.model.UserRole
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.screens.register.INVITATION_CODE_LENGTH

/** The dialogs of the family card. */
enum class FamilyDialog { Create, Join, Leave }

data class ProfileState(
    val user: User? = null,
    val family: Family? = null,
    val notificationsEnabled: Boolean = true,
    val inviting: Boolean = false,
    val loggingOut: Boolean = false,
    val mode: AppMode = AppMode.Parent,
    /** The phone can check a fingerprint / face (otherwise the section is hidden). */
    val biometricAvailable: Boolean = false,
    val biometricEnabled: Boolean = false,
    val biometricBusy: Boolean = false,
    /** A freshly created invitation code, shown in a dialog (and shared from there). */
    val invitation: Invitation? = null,
    val familyDialog: FamilyDialog? = null,
    val familyName: String = "",
    val joinCode: String = "",
    val familyBusy: Boolean = false,
) {
    /** Only people who are both parent and staff get the switch. */
    val canSwitchMode get() = user?.roles?.let { UserRole.Parent in it && UserRole.Staff in it } == true

    /** Only a parent starts a family or hands out codes. */
    val isParent get() = user?.roles?.contains(UserRole.Parent) == true

    /** The profile is loaded and the person is in no family: they can create one (parents) or join one. */
    val noFamily get() = user != null && user.familyId == null

    val joinCodeValid get() = joinCode.length == INVITATION_CODE_LENGTH

    /** Leaving as the only member deletes the family. */
    val lastMember get() = (family?.members?.size ?: 0) <= 1
}

/** What [ProfileContent] can ask for; keeps the content stateless and previewable. */
data class ProfileActions(
    val enableNotifications: () -> Unit,
    val invite: () -> Unit,
    val shareInvitation: () -> Unit,
    val dismissInvitation: () -> Unit,
    val openFamilyDialog: (FamilyDialog) -> Unit,
    val closeFamilyDialog: () -> Unit,
    val confirmFamilyDialog: () -> Unit,
    val onFamilyName: (String) -> Unit,
    val onJoinCode: (String) -> Unit,
    val logout: () -> Unit,
    val setMode: (AppMode) -> Unit,
    val toggleBiometric: () -> Unit,
)
