package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.remote.ApiException
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.*
import com.mirage.bob1.domain.repository.*

@Singleton
class ClubRepositoryImpl @Inject constructor(private val api: Bob1Api) : ClubRepository {
    private val mutex = Mutex()
    private var venues: Map<String, Venue>? = null

    override suspend fun divisions() = apiCall { api.divisions(ClubQueryDto(isActive = true, take = 100)).items.map { it.toDomain() } }

    override suspend fun venue(id: String): Result<Venue> = apiCall {
        val all = mutex.withLock {
            venues ?: api.venues(ClubQueryDto(take = 100)).items.map { it.toDomain() }.associateBy { it.id }.also { venues = it }
        }
        all[id] ?: throw ApiException(404, "venue.not_found", "Venue not found")
    }
}
