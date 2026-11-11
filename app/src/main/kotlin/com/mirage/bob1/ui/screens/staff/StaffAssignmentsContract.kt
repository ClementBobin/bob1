package com.mirage.bob1.ui.screens.staff

import androidx.compose.foundation.lazy.items
import com.mirage.bob1.domain.model.StaffAssignment

data class StaffAssignmentsState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val items: List<StaffAssignment> = emptyList(),
    val seasonPoints: Int? = null,
)

/** What [StaffAssignmentsContent] can ask for; keeps the content stateless and previewable. */
data class StaffAssignmentsActions(
    val load: () -> Unit,
    val open: (StaffAssignment) -> Unit,
    val pull: () -> Unit,
)
