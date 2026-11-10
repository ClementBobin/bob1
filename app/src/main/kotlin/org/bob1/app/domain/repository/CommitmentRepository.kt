package org.bob1.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bob1.app.domain.model.*

interface CommitmentRepository {
    fun observe(): Flow<List<Commitment>>
    suspend fun refresh(): Result<Unit>
}
