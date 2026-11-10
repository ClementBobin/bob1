package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.EmptyQueryDto
import com.mirage.bob1.data.dto.OpenSlotsQueryDto
import com.mirage.bob1.data.dto.OpenStaffMatchDto
import com.mirage.bob1.data.dto.StaffAssignmentDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import javax.inject.Inject

/** Role `Staff` only. */
internal class StaffAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /staff/assignments — my upcoming staff slots. */
    suspend fun getAssignments(): List<StaffAssignmentDto> =
        client.query("staff/assignments", EmptyQueryDto()).body()

    /** QUERY /staff/open-slots — matches with staff slots; the API defaults to the next 30 days. */
    suspend fun getOpenSlots(from: String? = null, to: String? = null): List<OpenStaffMatchDto> =
        client.query("staff/open-slots", OpenSlotsQueryDto(from, to)).body()
}
