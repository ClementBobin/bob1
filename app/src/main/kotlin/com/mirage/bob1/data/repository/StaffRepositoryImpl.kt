package com.mirage.bob1.data.repository

import com.mirage.bob1.data.remote.RankingAPI
import com.mirage.bob1.data.remote.StaffAPI
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.model.OpenStaffMatch
import com.mirage.bob1.domain.model.PointEntry
import com.mirage.bob1.domain.model.StaffAssignment
import com.mirage.bob1.domain.repository.StaffRepository
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class StaffRepositoryImpl @Inject constructor(
    private val staffAPI: StaffAPI,
    private val rankingAPI: RankingAPI,
) : StaffRepository {

    override suspend fun assignments(): Result<List<StaffAssignment>> =
        apiCall { staffAPI.getAssignments().map { it.toDomain() } }

    override suspend fun openSlots(from: Instant?, to: Instant?): Result<List<OpenStaffMatch>> =
        apiCall { staffAPI.getOpenSlots(from?.toString(), to?.toString()).map { it.toDomain() } }

    override suspend fun myPointEntries(): Result<List<PointEntry>> =
        apiCall { rankingAPI.getMyEntries().map { it.toDomain() } }
}
