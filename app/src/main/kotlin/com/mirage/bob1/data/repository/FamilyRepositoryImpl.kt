package com.mirage.bob1.data.repository

import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.*

@Singleton
class FamilyRepositoryImpl @Inject constructor(private val api: Bob1Api) : FamilyRepository {
    override suspend fun family() = apiCall { api.family().toDomain() }
    override suspend fun createInvitation() = apiCall { api.createInvitation().toDomain() }
}
