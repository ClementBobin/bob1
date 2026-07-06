package com.bob1.app.domain.repository

import com.bob1.app.domain.model.Location

interface LocationRepository {
    suspend fun getLocations(): Result<List<Location>>
    suspend fun createLocation(name: String, address: String): Result<Location>
}
