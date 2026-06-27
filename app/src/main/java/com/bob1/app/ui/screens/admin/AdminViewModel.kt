package com.bob1.app.ui.screens.admin

import android.app.Application
import com.bob1.app.domain.model.*
import com.bob1.app.domain.repository.AdminRepository
import com.bob1.app.domain.repository.DivisionRepository
import dev.kindling.compose.KViewModel
import org.koin.core.component.inject

object AdminContracts {
    data class UiState(
        val isLoading: Boolean = false,
        val settings: AppSettings? = null,
        val divisions: List<Division> = emptyList(),
        val teams: List<Team> = emptyList(),
        val pointRules: List<PointRule> = emptyList(),
        val error: String? = null,
        val showCreateDivision: Boolean = false,
        val newDivisionName: String = "",
        val showSettingsEdit: Boolean = false,
        val editJ15: String = "15",
        val editJ4: String = "4",
    )

    sealed interface UiEvent {
        data class ShowError(val msg: String) : UiEvent
        object SettingsSaved : UiEvent
    }
}

class AdminViewModel(
    application: Application
) : KViewModel<AdminContracts.UiState>(AdminContracts.UiState(), application) {

    private val adminRepo: AdminRepository       by inject()
    private val divisionRepo: DivisionRepository by inject()

    init { loadAll() }

    fun loadAll() {
        updateState { copy(isLoading = true) }
        fetchData(
            source = {
                Triple(
                    adminRepo.getSettings().getOrThrow(),
                    divisionRepo.getDivisions().getOrThrow(),
                    adminRepo.getPointRules().getOrThrow(),
                )
            },
            onResult = {
                onSuccess { (settings, divisions, rules) ->
                    updateState {
                        copy(
                            isLoading  = false,
                            settings   = settings,
                            divisions  = divisions,
                            pointRules = rules,
                            editJ15    = settings.confirmationOffsetJ15.toString(),
                            editJ4     = settings.confirmationOffsetJ4.toString(),
                        )
                    }
                }
                onFailure { e ->
                    updateState { copy(isLoading = false, error = e.message) }
                    sendEvent(AdminContracts.UiEvent.ShowError(e.message ?: "Erreur"))
                }
            }
        )
    }

    fun showSettingsEdit()    = updateState { copy(showSettingsEdit = true) }
    fun dismissSettingsEdit() = updateState { copy(showSettingsEdit = false) }
    fun onJ15Changed(v: String) = updateState { copy(editJ15 = v) }
    fun onJ4Changed(v: String)  = updateState { copy(editJ4  = v) }

    fun saveSettings() {
        val s   = state.value
        val j15 = s.editJ15.toIntOrNull() ?: run {
            sendEvent(AdminContracts.UiEvent.ShowError("Valeur invalide pour J-15")); return
        }
        val j4  = s.editJ4.toIntOrNull()  ?: run {
            sendEvent(AdminContracts.UiEvent.ShowError("Valeur invalide pour J-4")); return
        }
        fetchData(
            source   = { adminRepo.updateSettings(AppSettings(j15, j4)).getOrThrow() },
            onResult = {
                onSuccess { settings ->
                    updateState {
                        copy(
                            settings         = settings,
                            showSettingsEdit = false,
                            editJ15          = settings.confirmationOffsetJ15.toString(),
                            editJ4           = settings.confirmationOffsetJ4.toString(),
                        )
                    }
                    sendEvent(AdminContracts.UiEvent.SettingsSaved)
                }
                onFailure { e -> sendEvent(AdminContracts.UiEvent.ShowError(e.message ?: "Erreur")) }
            }
        )
    }

    fun showCreateDivision()    = updateState { copy(showCreateDivision = true, newDivisionName = "") }
    fun dismissCreateDivision() = updateState { copy(showCreateDivision = false) }
    fun onDivisionNameChanged(v: String) = updateState { copy(newDivisionName = v) }

    fun createDivision() {
        val name = state.value.newDivisionName.trim().takeIf { it.isNotEmpty() } ?: run {
            sendEvent(AdminContracts.UiEvent.ShowError("Le nom de la division ne peut pas être vide"))
            return
        }
        fetchData(
            source   = { adminRepo.createDivision(name).getOrThrow() },
            onResult = {
                onSuccess { division ->
                    updateState {
                        copy(divisions = divisions + division, showCreateDivision = false, newDivisionName = "")
                    }
                }
                onFailure { e -> sendEvent(AdminContracts.UiEvent.ShowError(e.message ?: "Erreur")) }
            }
        )
    }

    fun deleteDivision(id: String) = fetchData(
        source   = { adminRepo.deleteDivision(id).getOrThrow() },
        onResult = {
            onSuccess { updateState { copy(divisions = divisions.filter { it.id != id }) } }
            onFailure { e -> sendEvent(AdminContracts.UiEvent.ShowError(e.message ?: "Erreur")) }
        }
    )
}