package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.AccountAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Family
import com.mirage.bob1.domain.model.Invitation
import com.mirage.bob1.domain.repository.FamilyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FamilyRepositoryImpl @Inject constructor(
    private val accountAPI: AccountAPI,
) : FamilyRepository {
    override suspend fun family(): Result<Family> = apiCall { accountAPI.getFamily().toDomain() }
    override suspend fun createInvitation(): Result<Invitation> = apiCall { accountAPI.createInvitation().toDomain() }
}
