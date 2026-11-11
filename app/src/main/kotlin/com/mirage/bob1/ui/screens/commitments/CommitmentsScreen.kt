package com.mirage.bob1.ui.screens.commitments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mirage.bob1.ui.components.Bob1TopBar
import com.mirage.bob1.ui.components.EmptyBox
import com.mirage.bob1.ui.components.SectionCard
import com.mirage.bob1.ui.components.StatusBadge
import com.mirage.bob1.ui.components.Tone
import com.mirage.bob1.ui.components.commitments.typeLabel
import com.mirage.bob1.ui.core.formatShort
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler
import dev.kindling.compose.KScreen

@Composable
fun CommitmentsScreen(navController: NavController, viewModel: CommitmentsViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        CommitmentsContent(
            state = state,
            actions = CommitmentsActions(
                open = vm::open,
                refresh = vm::refresh,
            ),
        )
    }
}

@Composable
fun CommitmentsContent(state: CommitmentsState, actions: CommitmentsActions) {
    Column(Modifier.fillMaxSize()) {
        Bob1TopBar(title = "Mes engagements")
        PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = actions.refresh, modifier = Modifier.fillMaxSize()) {
            if (state.items.isEmpty() && !state.refreshing) {
                EmptyBox(Icons.Outlined.VerifiedUser, "Aucun engagement", "Vos présences, postes et covoiturages à venir apparaîtront ici.")
            } else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(state.items, key = { it.type.name + it.matchId + it.label }) { c ->
                    SectionCard(title = c.matchTitle, modifier = Modifier.clickable { actions.open(c) }, trailing = { StatusBadge(c.typeLabel(), Tone.Joined) }) {
                        Text(c.startsAt.formatShort(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
