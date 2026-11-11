package com.mirage.bob1.ui.screens.ranking

import android.app.Application
import com.mirage.bob1.domain.usecase.GetRankingUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    application: Application,
    private val getRanking: GetRankingUseCase,
) : Bob1ViewModel<RankingState>(RankingState(), application) {

    init { load() }

    fun setFamily(family: Boolean) { updateState { copy(family = family) }; load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getRanking(state.value.family) }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { r ->
            updateState { copy(loading = false, ranking = r) }
        }
    }
}
