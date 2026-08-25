package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.LocationAPI
import com.mirage.bob1.domain.model.Location
import com.mirage.bob1.domain.repository.LocationRepository

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
