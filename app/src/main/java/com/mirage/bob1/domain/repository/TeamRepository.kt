package com.mirage.bob1ob1.domain.repository

import com.mirage.bob1ob1.domain.model.Team

interface TeamRepository {
    suspend fun getTeams(divisionId: String? = null): Result<List<Team>>
    suspend fun createTeam(name: String, divisionId: String): Result<Team>
    suspend fun updateTeam(id: String, name: String): Result<Team>
    suspend fun deleteTeam(id: String): Result<Unit>
}
