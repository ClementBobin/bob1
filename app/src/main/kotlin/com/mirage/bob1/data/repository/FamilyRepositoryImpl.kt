package com.mirage.bob1.data.repository

import com.mirage.bob1.data.dto.AuthResponseDto
import com.mirage.bob1.data.remote.AccountAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.Family
import com.mirage.bob1.domain.model.Invitation
import com.mirage.bob1.domain.model.User
import com.mirage.bob1.domain.repository.FamilyRepository
import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.capacity.session.SecureSession

@Singleton
internal class FamilyRepositoryImpl @Inject constructor(
    private val accountAPI: AccountAPI,
    private val session: SecureSession,
) : FamilyRepository {
    override suspend fun family(): Result<Family> = apiCall { accountAPI.getFamily().toDomain() }
    override suspend fun createInvitation(): Result<Invitation> = apiCall { accountAPI.createInvitation().toDomain() }

    override suspend fun create(name: String?): Result<User> = apiCall { adopt(accountAPI.createFamily(name)) }
    override suspend fun join(code: String): Result<User> = apiCall { adopt(accountAPI.joinFamily(code)) }
    override suspend fun leave(): Result<User> = apiCall { adopt(accountAPI.leaveFamily()) }

    /** The family id is a claim of the access token: keep the fresh tokens the API sends back. */
    private suspend fun adopt(response: AuthResponseDto): User {
        session.save(response.accessToken, response.refreshToken)
        return response.user.toDomain()
    }
}
