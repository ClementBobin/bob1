package com.mirage.bob1.ui.screens.ranking

import com.mirage.bob1.domain.model.Ranking

data class RankingState(val family: Boolean = false, val loading: Boolean = true, val error: String? = null, val ranking: Ranking? = null)

/** What [RankingContent] can ask for; keeps the content stateless and previewable. */
data class RankingActions(
    val load: () -> Unit,
    val setFamily: (Boolean) -> Unit,
)
