package com.mirage.bob1.ui.screens.commitments

import androidx.compose.foundation.lazy.items
import com.mirage.bob1.domain.model.Commitment

data class CommitmentsState(val items: List<Commitment> = emptyList(), val refreshing: Boolean = false)

/** What [CommitmentsContent] can ask for; keeps the content stateless and previewable. */
data class CommitmentsActions(
    val open: (Commitment) -> Unit,
    val refresh: () -> Unit,
)
