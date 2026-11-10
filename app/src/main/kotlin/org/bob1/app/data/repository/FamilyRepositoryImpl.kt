package org.bob1.app.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.repository.*

@Singleton
class FamilyRepositoryImpl @Inject constructor(private val api: Bob1Api) : FamilyRepository {
    override suspend fun family() = apiCall { api.family().toDomain() }
    override suspend fun createInvitation() = apiCall { api.createInvitation().toDomain() }
}
