package com.mirage.bob1.domain.repository

import kotlinx.coroutines.flow.Flow
import com.mirage.bob1.domain.model.*

interface CommitmentRepository {
    fun observe(): Flow<List<Commitment>>
    suspend fun refresh(): Result<Unit>
}
