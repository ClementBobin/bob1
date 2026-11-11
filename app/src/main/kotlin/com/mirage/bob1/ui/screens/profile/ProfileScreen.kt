package com.mirage.bob1.ui.screens.profile

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
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.PersonAvatar
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.core.AppMode
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen
import dev.kindling.core.components.ui.avatar.KAvatarSize
import dev.kindling.core.components.ui.button.KButton
import dev.kindling.core.components.ui.button.KButtonVariant

@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        ProfileContent(
            state = state,
            actions = ProfileActions(
                enableNotifications = vm::enableNotifications,
                invite = vm::invite,
                logout = vm::logout,
                setMode = vm::setMode,
            ),
        )
    }
}

@Composable
fun ProfileContent(state: ProfileState, actions: ProfileActions) {
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
                            KButton("Parent", { actions.setMode(AppMode.Parent) }, Modifier.weight(1f), variant = if (state.mode == AppMode.Parent) KButtonVariant.Default else KButtonVariant.Outline)
                            KButton("Staff", { actions.setMode(AppMode.Staff) }, Modifier.weight(1f), variant = if (state.mode == AppMode.Staff) KButtonVariant.Default else KButtonVariant.Outline)
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
                        KButton("Inviter un parent", actions.invite, Modifier.fillMaxWidth(), variant = KButtonVariant.Secondary, isLoading = state.inviting)
                    }
                }
            }
            if (!state.notificationsEnabled) {
                item {
                    SectionCard(title = "Notifications") {
                        Text("Activez-les pour recevoir les rappels de match et les annonces du club.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        KButton("Activer les notifications", actions.enableNotifications, Modifier.fillMaxWidth())
                    }
                }
            }
            item { KButton("Se déconnecter", actions.logout, Modifier.fillMaxWidth(), variant = KButtonVariant.Outline, isLoading = state.loggingOut) }
        }
    }
}
