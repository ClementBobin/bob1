package com.mirage.bob1.ui.screens.staff

import com.mirage.bob1.domain.model.PointEntry
import com.mirage.bob1.domain.model.Ranking

data class StaffRankingState(
    val loading: Boolean = true,
    val error: String? = null,
    val ranking: Ranking? = null,
    val entries: List<PointEntry> = emptyList(),
)

/** What [StaffRankingContent] can ask for; keeps the content stateless and previewable. */
data class StaffRankingActions(
    val load: () -> Unit,
)
