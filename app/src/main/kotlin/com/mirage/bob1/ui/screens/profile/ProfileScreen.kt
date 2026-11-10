package org.bob1.app.ui.screens.profile

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.avatar.KAvatarSize
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant
import javax.inject.Inject
import kotlinx.coroutines.launch
import org.bob1.app.domain.model.Family
import org.bob1.app.domain.model.User
import org.bob1.app.domain.model.UserRole
import org.bob1.app.ui.core.AppMode
import org.bob1.app.ui.core.AppModeStore
import org.bob1.app.domain.usecase.*
import org.bob1.app.ui.core.Bob1ViewModel
import org.bob1.app.ui.core.components.*
import org.bob1.app.ui.core.navigation.ShareText
import org.bob1.app.ui.core.navigation.rememberUiEventHandler
import org.bob1.capacity.permission.NotificationPermission

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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val getMe: GetMeUseCase,
    private val getFamily: GetFamilyUseCase,
    private val createInvitation: CreateInvitationUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val permission: NotificationPermission,
    private val modeStore: AppModeStore,
) : Bob1ViewModel<ProfileState>(ProfileState(), application) {

    init {
        viewModelScope.launch { modeStore.mode.collect { m -> updateState { copy(mode = m) } } }
        viewModelScope.launch { permission.granted.collect { g -> updateState { copy(notificationsEnabled = g) } } }
        call({ getMe() }) { u -> updateState { copy(user = u) } }
        call({ getFamily() }, onError = {}) { f -> updateState { copy(family = f) } }
    }

    fun enableNotifications() = permission.request()
    fun setMode(mode: AppMode) = modeStore.set(mode)

    fun invite() {
        updateState { copy(inviting = true) }
        call({ createInvitation() }, onError = { updateState { copy(inviting = false) }; sendError(it) }) { inv ->
            updateState { copy(inviting = false) }
            sendEvent(ShareText("Rejoins ma famille sur BOB1 : crée ton compte, choisis « J'ai un code » et saisis ${inv.code} (valable 7 jours)."))
        }
    }

    fun logout() {
        updateState { copy(loggingOut = true) }
        viewModelScope.launch { logoutUseCase() } // session flips -> AppRoot goes back to login
    }
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Profil")
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                state.user?.let { u ->
                    item {
                        SectionCard(title = u.fullName) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                PersonAvatar(u.initials, KAvatarSize.Lg)
                                Column {
                                    Text(u.email, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(u.roles.joinToString(" · ") { it.name }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
                if (state.canSwitchMode) {
                    item {
                        SectionCard(title = "Mon espace") {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                KButton("Parent", { vm.setMode(AppMode.Parent) }, Modifier.weight(1f), variant = if (state.mode == AppMode.Parent) KButtonVariant.Default else KButtonVariant.Outline)
                                KButton("Staff", { vm.setMode(AppMode.Staff) }, Modifier.weight(1f), variant = if (state.mode == AppMode.Staff) KButtonVariant.Default else KButtonVariant.Outline)
                            }
                        }
                    }
                }
                state.family?.let { f ->
                    item {
                        SectionCard(title = "Famille ${f.name}") {
                            f.members.forEach { m ->
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    PersonAvatar(m.initials)
                                    Column(Modifier.weight(1f)) {
                                        Text(if (m.isMe) "${m.fullName} (vous)" else m.fullName, fontWeight = FontWeight.SemiBold)
                                        Text(m.divisionName ?: m.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            KButton("Inviter un parent", vm::invite, Modifier.fillMaxWidth(), variant = KButtonVariant.Secondary, isLoading = state.inviting)
                        }
                    }
                }
                if (!state.notificationsEnabled) {
                    item {
                        SectionCard(title = "Notifications") {
                            Text("Activez-les pour recevoir les rappels de match et les annonces du club.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            KButton("Activer les notifications", vm::enableNotifications, Modifier.fillMaxWidth())
                        }
                    }
                }
                item { KButton("Se déconnecter", vm::logout, Modifier.fillMaxWidth(), variant = KButtonVariant.Outline, isLoading = state.loggingOut) }
            }
        }
    }
}
