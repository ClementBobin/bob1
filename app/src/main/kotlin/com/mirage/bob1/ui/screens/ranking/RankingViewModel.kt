package com.mirage.bob1.ui.screens.ranking

import android.app.Application
import com.mirage.bob1.domain.usecase.GetRankingUseCase
import com.mirage.bob1.domain.usecase.GetSeasonsUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    application: Application,
    private val getRanking: GetRankingUseCase,
    private val getSeasons: GetSeasonsUseCase,
) : Bob1ViewModel<RankingState>(RankingState(), application) {

    init {
        call({ getSeasons() }, onError = {}) { s -> updateState { copy(seasons = s) } }
        load()
    }

    fun setFamily(family: Boolean) { updateState { copy(family = family) }; load() }
    fun selectSeason(label: String?) { updateState { copy(season = label) }; load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getRanking(state.value.family, season = state.value.season) }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { r ->
            updateState { copy(loading = false, ranking = r) }
        }
    }
}
