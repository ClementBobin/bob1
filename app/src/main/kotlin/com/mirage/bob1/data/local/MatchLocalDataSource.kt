package com.mirage.bob1.data.local

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.mirage.bob1.domain.model.*
import org.bob1.room.dao.MatchDao
import org.bob1.room.entity.MatchEntity

// Room lives in :room and knows nothing about the domain; the mapping happens here.

private fun MatchEntity.toDomain() = Match(
    id, title, divisionId, divisionName, homeTeamId, awayTeamId, venueId, Instant.ofEpochMilli(startsAtUtc),
    MatchStatus.entries.firstOrNull { it.name == status } ?: MatchStatus.Scheduled,
)

private fun Match.toEntity() = MatchEntity(id, title, divisionId, divisionName, homeTeamId, awayTeamId, venueId, startsAt.toEpochMilli(), status.name)

@Singleton
class MatchLocalDataSource @Inject constructor(private val dao: MatchDao) {
    fun observe(from: Instant, to: Instant): Flow<List<Match>> =
        dao.observeBetween(from.toEpochMilli(), to.toEpochMilli()).map { list -> list.map { it.toDomain() } }

    suspend fun get(id: String): Match? = dao.get(id)?.toDomain()

    suspend fun replace(from: Instant, to: Instant, items: List<Match>) =
        dao.replaceBetween(from.toEpochMilli(), to.toEpochMilli(), items.map { it.toEntity() })
}
