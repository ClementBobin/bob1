package com.mirage.bob1.domain.repository

import java.time.Instant
import kotlinx.coroutines.flow.Flow
import com.mirage.bob1.domain.model.*

interface MatchRepository {
    fun observeMatches(from: Instant, to: Instant): Flow<List<Match>>
    suspend fun refreshMatches(from: Instant, to: Instant): Result<Unit>
    suspend fun getMatch(id: String): Result<Match>
    suspend fun participation(matchId: String): Result<Participation>
    suspend fun confirmPresence(matchId: String): Result<Unit>
    suspend fun joinSlot(slotId: String): Result<Unit>
    suspend fun leaveSlot(slotId: String): Result<Unit>
}
