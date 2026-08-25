package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.SeasonPointRankingDto
import com.mirage.bob1.data.dto.SeasonPointSummaryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal class SeasonPointAPI(private val client: HttpClient) {

    /** GET /api/ranking?season={season}
     *  Returns the full leaderboard for the given season (current season if omitted). */
    suspend fun getRanking(season: Int? = null): List<SeasonPointRankingDto> =
        client.get("/api/ranking") {
            season?.let { parameter("season", it) }
        }.body()

    /** GET /api/ranking/me?season={season}
     *  Returns the authenticated user's season point summary. */
    suspend fun getMyRanking(season: Int? = null): SeasonPointSummaryDto =
        client.get("/api/ranking/me") {
            season?.let { parameter("season", it) }
        }.body()

    /** GET /api/ranking/users/{userId}?season={season}
     *  Returns a specific user's season point summary. */
    suspend fun getUserRanking(userId: String, season: Int? = null): SeasonPointSummaryDto =
        client.get("/api/ranking/users/$userId") {
            season?.let { parameter("season", it) }
        }.body()
}