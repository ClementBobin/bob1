package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.Location

interface LocationRepository {
    suspend fun getLocations(): Result<List<Location>>
    suspend fun createLocation(name: String, address: String): Result<Location>
}
