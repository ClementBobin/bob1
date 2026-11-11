package com.mirage.bob1.ui.screens.commitments

import android.app.Application
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewModelScope
import com.mirage.bob1.domain.model.Commitment
import com.mirage.bob1.domain.usecase.ObserveCommitmentsUseCase
import com.mirage.bob1.domain.usecase.RefreshCommitmentsUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.navigation.MatchDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

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
