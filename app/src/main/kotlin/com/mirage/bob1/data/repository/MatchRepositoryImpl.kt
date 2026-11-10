package com.mirage.bob1.data.repository

import com.mirage.bob1.data.local.MatchLocalDataSource
import com.mirage.bob1.data.remote.MatchAPI
import com.mirage.bob1.data.remote.ParticipationAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Match
import com.mirage.bob1.domain.model.Participation
import com.mirage.bob1.domain.repository.MatchRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
internal class MatchRepositoryImpl @Inject constructor(
    private val matchAPI: MatchAPI,
    private val participationAPI: ParticipationAPI,
    private val local: MatchLocalDataSource,
) : MatchRepository {

    override fun observeMatches(from: Instant, to: Instant): Flow<List<Match>> = local.observe(from, to)

    override suspend fun refreshMatches(from: Instant, to: Instant): Result<Unit> = apiCall {
        val matches = matchAPI.getMatches(from.toString(), to.toString()).map { it.toDomain() }
        local.replace(from, to, matches)
    }

    override suspend fun getMatch(id: String): Result<Match> =
        local.get(id)?.let { Result.success(it) } ?: apiCall { matchAPI.getMatch(id).toDomain() }

    override suspend fun participation(matchId: String): Result<Participation> =
        apiCall { participationAPI.getParticipation(matchId).toDomain() }

    override suspend fun confirmPresence(matchId: String): Result<Unit> = apiCall { participationAPI.confirmPresence(matchId) }
    override suspend fun joinSlot(slotId: String): Result<Unit> = apiCall { participationAPI.joinSlot(slotId) }
    override suspend fun leaveSlot(slotId: String): Result<Unit> = apiCall { participationAPI.leaveSlot(slotId) }
}
