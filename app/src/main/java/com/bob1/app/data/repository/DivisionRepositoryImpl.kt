package com.bob1.app.data.repository

import com.bob1.app.data.remote.DivisionAPI
import com.bob1.app.domain.model.Division
import com.bob1.app.domain.repository.DivisionRepository

/**
 * Implémentation de [DivisionRepository] utilisant [DivisionAPI]
 */
internal class DivisionRepositoryImpl(
    private val divisionAPI: DivisionAPI
) : DivisionRepository {
    override suspend fun getDivisions(): Result<List<Division>> = runCatching {
        divisionAPI.getDivisions().map { it.toDomain() }
    }
}