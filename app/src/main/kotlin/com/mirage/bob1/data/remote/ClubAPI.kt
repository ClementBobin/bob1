package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.ClubQueryDto
import com.mirage.bob1.data.dto.DivisionDto
import com.mirage.bob1.data.dto.EmptyQueryDto
import com.mirage.bob1.data.dto.PagedDto
import com.mirage.bob1.data.dto.SeasonDto
import com.mirage.bob1.data.dto.VenueDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import javax.inject.Inject

internal class ClubAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /seasons — every season that exists, newest first (1 Sept → 31 Aug, label `2026-2027`). */
    suspend fun getSeasons(): List<SeasonDto> =
        client.query("seasons", EmptyQueryDto()).body()

    /** QUERY /divisions — active divisions only. */
    suspend fun getDivisions(): List<DivisionDto> =
        client.query("divisions", ClubQueryDto(isActive = true, take = 100)).body<PagedDto<DivisionDto>>().items

    /** QUERY /venues */
    suspend fun getVenues(): List<VenueDto> =
        client.query("venues", ClubQueryDto(take = 100)).body<PagedDto<VenueDto>>().items
}
