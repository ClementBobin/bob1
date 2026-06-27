package com.bob1.app.data.repository

import com.bob1.app.data.remote.TeamAPI
import com.bob1.app.domain.model.Team
import com.bob1.app.domain.repository.TeamRepository

/**
 * Implémentation de [TeamRepository] utilisant [TeamAPI]
 */
internal class TeamRepositoryImpl(
    private val teamAPI: TeamAPI
) : TeamRepository {

    override suspend fun getTeams(divisionId: String?): Result<List<Team>> = runCatching {
        teamAPI.getTeams(divisionId).map { it.toDomain() }
    }

    override suspend fun createTeam(name: String, divisionId: String): Result<Team> = runCatching {
        teamAPI.createTeam(name, divisionId).toDomain()
    }

    override suspend fun updateTeam(id: String, name: String): Result<Team> = runCatching {
        teamAPI.updateTeam(id, name).toDomain()
    }

    override suspend fun deleteTeam(id: String): Result<Unit> = runCatching {
        teamAPI.deleteTeam(id)
    }
}