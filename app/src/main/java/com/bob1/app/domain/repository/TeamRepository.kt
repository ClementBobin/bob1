package com.bob1.app.domain.repository

import com.bob1.app.domain.model.Team

interface TeamRepository {
    suspend fun getTeams(divisionId: String? = null): Result<List<Team>>
    suspend fun createTeam(name: String, divisionId: String): Result<Team>
    suspend fun updateTeam(id: String, name: String): Result<Team>
    suspend fun deleteTeam(id: String): Result<Unit>
}
