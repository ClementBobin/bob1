package com.bob1.app.domain.repository

import com.bob1.app.domain.model.Division

interface DivisionRepository {
    suspend fun getDivisions(): Result<List<Division>>
}
