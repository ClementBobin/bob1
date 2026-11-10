package com.mirage.bob1.ui.screens.commitments

import android.app.Application
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kindling.compose.KScreen
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.mirage.bob1.domain.model.Commitment
import com.mirage.bob1.domain.model.CommitmentType
import com.mirage.bob1.domain.usecase.ObserveCommitmentsUseCase
import com.mirage.bob1.domain.usecase.RefreshCommitmentsUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.components.*
import com.mirage.bob1.ui.core.formatShort
import com.mirage.bob1.ui.core.navigation.MatchDestination
import com.mirage.bob1.ui.core.navigation.rememberUiEventHandler

data class CommitmentsState(val items: List<Commitment> = emptyList(), val refreshing: Boolean = false)

@HiltViewModel
class CommitmentsViewModel @Inject constructor(
    application: Application,
    observe: ObserveCommitmentsUseCase,
    private val refreshUseCase: RefreshCommitmentsUseCase,
) : Bob1ViewModel<CommitmentsState>(CommitmentsState(), application) {

    init {
        viewModelScope.launch { observe().collect { list -> updateState { copy(items = list) } } }
        refresh()
    }

    fun refresh() {
        updateState { copy(refreshing = true) }
        call({ refreshUseCase() }, onError = { updateState { copy(refreshing = false) }; sendError(it) }) { updateState { copy(refreshing = false) } }
    }

    fun open(c: Commitment) = navigate(MatchDestination(c.matchId))
}

private fun Commitment.typeLabel() = when (type) {
    CommitmentType.Presence -> "Présence confirmée"
    CommitmentType.Volunteer -> "Bénévolat" + if (label.isNotBlank()) " · $label" else ""
    CommitmentType.Staff -> "Staff" + if (label.isNotBlank()) " · $label" else ""
    CommitmentType.CarpoolDriver -> "Conducteur"
    CommitmentType.CarpoolPassenger -> "Passager"
    CommitmentType.Other -> label.ifBlank { "Engagement" }
}

@Composable
fun CommitmentsScreen(navController: NavController, viewModel: CommitmentsViewModel = hiltViewModel()) {
    val handle = rememberUiEventHandler()
    KScreen(viewModel = viewModel, navController = navController, onEvent = { _, _, e -> handle(e) }) { state, vm ->
        Column(Modifier.fillMaxSize()) {
            Bob1TopBar(title = "Mes engagements")
            PullToRefreshBox(isRefreshing = state.refreshing, onRefresh = vm::refresh, modifier = Modifier.fillMaxSize()) {
                if (state.items.isEmpty() && !state.refreshing) {
                    EmptyBox(Icons.Outlined.VerifiedUser, "Aucun engagement", "Vos présences, postes et covoiturages à venir apparaîtront ici.")
                } else LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.items, key = { it.type.name + it.matchId + it.label }) { c ->
                        SectionCard(title = c.matchTitle, modifier = Modifier.clickable { vm.open(c) }, trailing = { StatusBadge(c.typeLabel(), Tone.Joined) }) {
                            Text(c.startsAt.formatShort(), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
