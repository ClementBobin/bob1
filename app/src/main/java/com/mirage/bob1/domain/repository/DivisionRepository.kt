package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.Division

interface DivisionRepository {
    suspend fun getDivisions(): Result<List<Division>>
}
