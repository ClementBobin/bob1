package com.bob1.app.data.repository

import com.bob1.app.data.dto.OfficialRole
import com.bob1.app.data.remote.MatchAPI
import com.bob1.app.domain.model.Match
import com.bob1.app.domain.repository.MatchRepository

/**
 * Implémentation de [MatchRepository] utilisant [MatchAPI]
 */
internal class MatchRepositoryImpl(
    private val matchAPI: MatchAPI
) : MatchRepository {

    override suspend fun getMatches(
        divisionId: String?,
        year: Int,
        month: Int
    ): Result<List<Match>> = runCatching {
        matchAPI.getMatches(divisionId, year, month).map { it.toDomain() }
    }

    override suspend fun getMatch(matchId: String): Result<Match> = runCatching {
        matchAPI.getMatch(matchId).toDomain()
    }

    override suspend fun subscribeToMatch(
        matchId: String,
        role: OfficialRole
    ): Result<Match> = runCatching {
        matchAPI.subscribeToMatch(matchId, role.name).toDomain()
    }

    override suspend fun unsubscribeFromMatch(matchId: String): Result<Match> = runCatching {
        matchAPI.unsubscribeFromMatch(matchId).toDomain()
    }

    override suspend fun confirmPresence(matchId: String): Result<Match> = runCatching {
        matchAPI.confirmPresence(matchId).toDomain()
    }
}