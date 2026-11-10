package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.CommitmentDto
import com.mirage.bob1.data.dto.EmptyQueryDto
import com.mirage.bob1.data.dto.ParticipationDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import javax.inject.Inject

internal class ParticipationAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    suspend fun getParticipation(matchId: String): ParticipationDto =
        client.get("matches/$matchId/participation").body()

    suspend fun confirmPresence(matchId: String) {
        client.post("matches/$matchId/presence/confirm").ok()
    }

    suspend fun joinSlot(slotId: String) {
        client.post("slots/$slotId/join").ok()
    }

    suspend fun leaveSlot(slotId: String) {
        client.delete("slots/$slotId/join").ok()
    }

    /** QUERY /me/commitments — presence, volunteer / staff slots and carpools of the family. */
    suspend fun getCommitments(): List<CommitmentDto> =
        client.query("me/commitments", EmptyQueryDto()).body()
}
