package org.bob1.app.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.dto.*
import org.bob1.app.data.local.*
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.repository.*

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
