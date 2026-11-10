package com.mirage.bob1.domain.repository

import java.time.Instant
import com.mirage.bob1.domain.model.*

interface StaffRepository {
    suspend fun assignments(): Result<List<StaffAssignment>>
    suspend fun openSlots(from: Instant?, to: Instant?): Result<List<OpenStaffMatch>>
    suspend fun myPointEntries(): Result<List<PointEntry>>
}
