package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.EntryDto
import com.mirage.bob1.data.dto.FamilyRankingDto
import com.mirage.bob1.data.dto.MyEntriesQueryDto
import com.mirage.bob1.data.dto.PersonRankingDto
import com.mirage.bob1.data.dto.RankingQueryDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import javax.inject.Inject

internal class RankingAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** QUERY /rankings — [category] `null` = all, `"staff"` = officials. */
    suspend fun getPersonRanking(category: String?): PersonRankingDto =
        client.query("rankings", RankingQueryDto(category = category, take = 50)).body()

    /** QUERY /rankings with `view = "family"` answers with another shape. */
    suspend fun getFamilyRanking(category: String?): FamilyRankingDto =
        client.query("rankings", RankingQueryDto(category = category, view = "family", take = 50)).body()

    /** QUERY /rankings/me/entries — the points ledger of the signed-in person. */
    suspend fun getMyEntries(): List<EntryDto> =
        client.query("rankings/me/entries", MyEntriesQueryDto(take = 50)).body()
}
