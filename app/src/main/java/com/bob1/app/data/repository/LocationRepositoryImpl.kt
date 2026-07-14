package com.bob1.app.data.repository

import com.bob1.app.data.remote.LocationAPI
import com.bob1.app.domain.model.Location
import com.bob1.app.domain.repository.LocationRepository

internal class LocationRepositoryImpl(
    private val locationAPI: LocationAPI
) : LocationRepository {

    override suspend fun getLocations(): Result<List<Location>> = runCatching {
        locationAPI.getLocations().map {
            Location(id = it.id, name = it.name, address = it.address)
        }
    }

    // Non-admin app: no create/update/delete
    override suspend fun createLocation(name: String, address: String): Result<Location> =
        Result.failure(UnsupportedOperationException("Admin-only operation"))
}
