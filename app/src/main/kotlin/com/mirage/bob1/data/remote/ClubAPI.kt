package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.ClubQueryDto
import com.mirage.bob1.data.dto.DivisionDto
import com.mirage.bob1.data.dto.PagedDto
import com.mirage.bob1.data.dto.VenueDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import javax.inject.Inject

internal class ClubAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /divisions — active divisions only. */
    suspend fun getDivisions(): List<DivisionDto> =
        client.query("divisions", ClubQueryDto(isActive = true, take = 100)).body<PagedDto<DivisionDto>>().items

    /** QUERY /venues */
    suspend fun getVenues(): List<VenueDto> =
        client.query("venues", ClubQueryDto(take = 100)).body<PagedDto<VenueDto>>().items
}
