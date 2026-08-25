package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.DivisionAPI
import com.mirage.bob1.domain.model.Division
import com.mirage.bob1.domain.repository.DivisionRepository

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