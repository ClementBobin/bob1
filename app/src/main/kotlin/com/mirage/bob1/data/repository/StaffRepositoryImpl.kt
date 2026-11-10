package org.bob1.app.data.repository

import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import org.bob1.app.data.dto.*
import org.bob1.app.data.remote.Bob1Api
import org.bob1.app.data.remote.apiCall
import org.bob1.app.domain.repository.*

@Singleton
class StaffRepositoryImpl @Inject constructor(private val api: Bob1Api) : StaffRepository {
    override suspend fun assignments() = apiCall { api.staffAssignments(EmptyQueryDto()).map { it.toDomain() } }

    override suspend fun openSlots(from: Instant?, to: Instant?) =
        apiCall { api.openStaffSlots(OpenSlotsQueryDto(from?.toString(), to?.toString())).map { it.toDomain() } }

    override suspend fun myPointEntries() = apiCall { api.myEntries(MyEntriesQueryDto(take = 50)).map { it.toDomain() } }
}
