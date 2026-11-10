package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.local.*
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.*

@Singleton
class CommitmentRepositoryImpl @Inject constructor(
    private val api: Bob1Api,
    private val local: CommitmentLocalDataSource,
) : CommitmentRepository {
    override fun observe() = local.observe()

    override suspend fun refresh(): Result<Unit> = apiCall {
        local.replace(api.commitments(EmptyQueryDto()).map { it.type to it.toDomain() })
    }
}
