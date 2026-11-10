package org.bob1.app.domain.repository

import java.time.Instant
import org.bob1.app.domain.model.*

interface StaffRepository {
    suspend fun assignments(): Result<List<StaffAssignment>>
    suspend fun openSlots(from: Instant?, to: Instant?): Result<List<OpenStaffMatch>>
    suspend fun myPointEntries(): Result<List<PointEntry>>
}
