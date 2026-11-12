package com.mirage.bob1.ui.screens.staff

import android.app.Application
import com.mirage.bob1.domain.usecase.GetMyPointEntriesUseCase
import com.mirage.bob1.domain.usecase.GetSeasonsUseCase
import com.mirage.bob1.domain.usecase.GetStaffRankingUseCase
import com.mirage.bob1.ui.core.Bob1ViewModel
import com.mirage.bob1.ui.core.userMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffRankingViewModel @Inject constructor(
    application: Application,
    private val getRanking: GetStaffRankingUseCase,
    private val getEntries: GetMyPointEntriesUseCase,
    private val getSeasons: GetSeasonsUseCase,
) : Bob1ViewModel<StaffRankingState>(StaffRankingState(), application) {

    init {
        call({ getSeasons() }, onError = {}) { s -> updateState { copy(seasons = s) } }
        load()
    }

    fun selectSeason(label: String?) { updateState { copy(season = label) }; load() }

    fun load() {
        updateState { copy(loading = true, error = null) }
        call({ getRanking(state.value.season) }, onError = { updateState { copy(loading = false, error = it.userMessage()) } }) { r ->
            updateState { copy(loading = false, ranking = r) }
        }
        call({ getEntries(state.value.season) }, onError = {}) { e -> updateState { copy(entries = e) } }
    }
}
