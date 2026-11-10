package org.bob1.app.data.local

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bob1.app.domain.model.*
import org.bob1.room.dao.CommitmentDao
import org.bob1.room.entity.CommitmentEntity

private fun CommitmentEntity.toDomain() = Commitment(
    CommitmentType.fromApi(type), matchId, matchTitle, Instant.ofEpochMilli(startsAtUtc), label,
)

private fun Commitment.toEntity(rawType: String) = CommitmentEntity(rawType, matchId, matchTitle, startsAt.toEpochMilli(), label)

@Singleton
class CommitmentLocalDataSource @Inject constructor(private val dao: CommitmentDao) {
    fun observe(): Flow<List<Commitment>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun replace(items: List<Pair<String, Commitment>>) =
        dao.replaceAll(items.map { (rawType, c) -> c.toEntity(rawType) })
}
