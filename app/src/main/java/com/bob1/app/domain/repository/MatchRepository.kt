package com.bob1.app.domain.repository

import com.bob1.app.data.dto.OfficialRole
import com.bob1.app.domain.model.Match

interface MatchRepository {
    suspend fun getMatches(divisionId: String? = null, year: Int, month: Int): Result<List<Match>>
    suspend fun getMatch(matchId: String): Result<Match>
    suspend fun subscribeToMatch(matchId: String, role: OfficialRole): Result<Match>
    suspend fun unsubscribeFromMatch(matchId: String): Result<Match>
    suspend fun confirmPresence(matchId: String): Result<Match>
}
