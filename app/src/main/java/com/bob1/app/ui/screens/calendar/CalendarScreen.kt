package com.bob1.app.ui.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bob1.app.data.dto.OfficialRole
import com.bob1.app.domain.model.*
import com.bob1.app.ui.core.components.ui.AppBottomBar
import com.bob1.app.ui.core.components.ui.calendar.*
import dev.kindling.compose.KScreen

@Composable
fun CalendarScreen(
    navController: NavController,
) {
    KScreen(
        viewModel = viewModel<CalendarViewModel>(),
        navController = navController
    ) { state, viewModel ->
        Scaffold(
            topBar = {
                CalendarTopBar(
                    unreadCount    = state.unreadCount,
                    onNotifClicked = viewModel::onNotificationsTapped,
                )
            },
            bottomBar = { AppBottomBar(navController) }
        ) { paddingValues ->
            CalendarContent(
                state = state,
                paddingValues = paddingValues,
                onDivisionToggled = viewModel::toggleDivision,
                onAllDivisionsSelected = viewModel::selectAllDivisions,
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
                onDayTapped = viewModel::onDayTapped,
                onMatchSelected = viewModel::selectMatchFromSheet,
                onDismissSheet = viewModel::dismissSheet,
                onRoleTapped = viewModel::onRoleTapped,
                onConfirmAction = viewModel::confirmRoleAction,
                onDismissConfirmDialog = viewModel::dismissConfirmDialog
            )
        }
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun CalendarContent(
    state: CalendarContracts.UiState,
    paddingValues: PaddingValues,
    onDivisionToggled: (String) -> Unit = {},
    onAllDivisionsSelected: () -> Unit = {},
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onDayTapped: (Int) -> Unit = {},
    onMatchSelected: (Match) -> Unit = {},
    onDismissSheet: () -> Unit = {},
    onRoleTapped: (Match, OfficialRole) -> Unit = { _, _ -> },
    onConfirmAction: () -> Unit = {},
    onDismissConfirmDialog: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)  // Fixed: use paddingValues parameter
    ) {
        // Division filter
        DivisionFilterBar(
            divisions           = state.divisions,
            selectedDivisionIds = state.selectedDivisionIds,
            onAllSelected       = onAllDivisionsSelected,
            onDivisionToggled   = onDivisionToggled,
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Calendar grid
                MonthCalendar(
                    year                = state.year,
                    month               = state.month,
                    matches             = state.matches,
                    selectedDivisionIds = state.selectedDivisionIds,
                    onDayTapped         = onDayTapped,
                    modifier            = Modifier.weight(1f),
                )

                // Month navigation
                MonthNavigator(
                    year         = state.year,
                    month        = state.month,
                    onPrevious   = onPreviousMonth,
                    onNext       = onNextMonth,
                )
            }
        }
    }

    // Bottom sheet: day match list
    if (state.selectedDayMatches.isNotEmpty()) {
        DayMatchListSheet(
            matches         = state.selectedDayMatches,
            onMatchSelected = onMatchSelected,
            onDismiss       = onDismissSheet,
        )
    }

    // Bottom sheet: match detail
    state.sheetMatch?.let { match ->
        MatchDetailSheet(
            match       = match,
            onRoleTap   = { role -> onRoleTapped(match, role) },
            onDismiss   = onDismissSheet,
        )
    }

    // Confirm dialog
    if (state.confirmDialogRole != null && state.confirmDialogAction != null) {
        ConfirmRoleDialog(
            role     = state.confirmDialogRole,
            action   = state.confirmDialogAction,
            onYes    = onConfirmAction,
            onNo     = onDismissConfirmDialog,
        )
    }
}