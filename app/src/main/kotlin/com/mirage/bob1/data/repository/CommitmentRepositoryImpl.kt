package com.mirage.bob1.data.repository

import com.mirage.bob1.data.local.CommitmentLocalDataSource
import com.mirage.bob1.data.remote.ParticipationAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Commitment
import com.mirage.bob1.domain.repository.CommitmentRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
internal class CommitmentRepositoryImpl @Inject constructor(
    private val participationAPI: ParticipationAPI,
    private val local: CommitmentLocalDataSource,
) : CommitmentRepository {

    override fun observe(): Flow<List<Commitment>> = local.observe()

    override suspend fun refresh(): Result<Unit> = apiCall {
        local.replace(participationAPI.getCommitments().map { it.type to it.toDomain() })
    }
}
