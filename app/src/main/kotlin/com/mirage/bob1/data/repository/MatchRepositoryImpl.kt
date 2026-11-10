package org.bob1.app.data.repository

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.dto.*
import org.bob1.app.data.local.*
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.model.*
import org.bob1.app.domain.repository.*

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val api: Bob1Api,
    private val local: MatchLocalDataSource,
) : MatchRepository {

    override fun observeMatches(from: Instant, to: Instant) = local.observe(from, to)

    override suspend fun refreshMatches(from: Instant, to: Instant): Result<Unit> = apiCall {
        val page = api.matches(MatchQueryDto(from = from.toString(), to = to.toString(), take = 300))
        local.replace(from, to, page.items.map { it.toDomain() })
    }

    override suspend fun getMatch(id: String): Result<Match> =
        local.get(id)?.let { Result.success(it) } ?: apiCall { api.match(id).toDomain() }

    override suspend fun participation(matchId: String) = apiCall { api.participation(matchId).toDomain() }
    override suspend fun confirmPresence(matchId: String) = apiCall { api.confirmPresence(matchId) }
    override suspend fun joinSlot(slotId: String) = apiCall { api.joinSlot(slotId) }
    override suspend fun leaveSlot(slotId: String) = apiCall { api.leaveSlot(slotId) }
}
