package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.ClubAPI
import com.mirage.bob1.data.remote.HttpException
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Season
import com.mirage.bob1.domain.model.Team
import com.mirage.bob1.domain.model.Venue
import com.mirage.bob1.domain.repository.ClubRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
internal class ClubRepositoryImpl @Inject constructor(
    private val clubAPI: ClubAPI,
) : ClubRepository {
    private val mutex = Mutex()
    private var venues: Map<String, Venue>? = null

    override suspend fun teams(): Result<List<Team>> =
        apiCall { clubAPI.getTeams().map { it.toDomain() } }

    override suspend fun seasons(): Result<List<Season>> =
        apiCall { clubAPI.getSeasons().map { it.toDomain() } }

    override suspend fun venue(id: String): Result<Venue> = apiCall {
        val all = mutex.withLock {
            venues ?: clubAPI.getVenues().map { it.toDomain() }.associateBy { it.id }.also { venues = it }
        }
        all[id] ?: throw HttpException.ClientError(404, "Salle introuvable.", "venue.not_found")
    }
}
