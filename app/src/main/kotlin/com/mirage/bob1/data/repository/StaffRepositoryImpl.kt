package com.mirage.bob1.data.repository

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import com.mirage.bob1.data.dto.*
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.apiCall
import com.mirage.bob1.domain.repository.*

@Singleton
class StaffRepositoryImpl @Inject constructor(private val api: Bob1Api) : StaffRepository {
    override suspend fun assignments() = apiCall { api.staffAssignments(EmptyQueryDto()).map { it.toDomain() } }

    override suspend fun openSlots(from: Instant?, to: Instant?) =
        apiCall { api.openStaffSlots(OpenSlotsQueryDto(from?.toString(), to?.toString())).map { it.toDomain() } }

    override suspend fun myPointEntries() = apiCall { api.myEntries(MyEntriesQueryDto(take = 50)).map { it.toDomain() } }
}
