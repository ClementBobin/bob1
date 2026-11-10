package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.MatchDto
import com.mirage.bob1.data.dto.MatchQueryDto
import com.mirage.bob1.data.dto.PagedDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

internal class MatchAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /matches — [from] / [to] are ISO-8601 UTC instants. */
    suspend fun getMatches(from: String, to: String): List<MatchDto> =
        client.query("matches", MatchQueryDto(from = from, to = to, take = 300)).body<PagedDto<MatchDto>>().items

    suspend fun getMatch(matchId: String): MatchDto = client.get("matches/$matchId").body()
}
