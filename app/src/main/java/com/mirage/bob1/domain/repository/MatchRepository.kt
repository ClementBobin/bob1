package com.mirage.bob1ob1.domain.repository

import com.mirage.bob1ob1.data.dto.OfficialRole
import com.mirage.bob1ob1.domain.model.Match

interface MatchRepository {
    suspend fun getMatches(year: Int, month: Int): Result<List<Match>>
    suspend fun getMatchesByDivision(divisionId: String): Result<List<Match>>
    suspend fun getMatch(matchId: String): Result<Match>
    suspend fun subscribeToMatch(matchId: String, role: OfficialRole): Result<Match>
    suspend fun unsubscribeFromMatch(matchId: String): Result<Match>
    suspend fun confirmPresence(matchId: String): Result<Match>
}
