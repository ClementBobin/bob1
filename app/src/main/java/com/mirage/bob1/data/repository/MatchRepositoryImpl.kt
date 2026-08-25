package com.mirage.bob1.data.repository

import com.mirage.bob1.data.dto.OfficialRole
import com.mirage.bob1.data.remote.MatchAPI
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.repository.MatchRepository

internal class MatchRepositoryImpl(
    private val matchAPI: MatchAPI
) : MatchRepository {

    override suspend fun getMatches(year: Int, month: Int): Result<List<Match>> = runCatching {
        matchAPI.getMatches(year, month).map { it.toDomain() }  // MinMatchDto.toDomain()
    }

    override suspend fun getMatchesByDivision(divisionId: String): Result<List<Match>> = runCatching {
        matchAPI.getMatchesByDivision(divisionId).map { it.toDomain() }
    }

    override suspend fun getMatch(matchId: String): Result<Match> = runCatching {
        matchAPI.getMatch(matchId).toDomain()
    }

    override suspend fun subscribeToMatch(matchId: String, role: OfficialRole): Result<Match> = runCatching {
        matchAPI.subscribeToMatch(matchId, role).toDomain()
    }

    override suspend fun unsubscribeFromMatch(matchId: String): Result<Match> = runCatching {
        matchAPI.unsubscribeFromMatch(matchId).toDomain()
    }

    override suspend fun confirmPresence(matchId: String): Result<Match> = runCatching {
        matchAPI.confirmPresence(matchId).toDomain()
    }
}