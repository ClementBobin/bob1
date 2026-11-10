package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.FamilyDto
import com.mirage.bob1.data.dto.InvitationDto
import com.mirage.bob1.data.dto.RefreshRequestDto
import com.mirage.bob1.data.dto.UserDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import javax.inject.Inject

internal class AccountAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    /** GET /me */
    suspend fun getMe(): UserDto = client.get("me").body()

    /** POST /auth/logout — revokes the refresh token. */
    suspend fun logout(refreshToken: String) {
        client.post("auth/logout") { setBodyJson(RefreshRequestDto(refreshToken)) }.ok()
    }

    suspend fun getFamily(): FamilyDto = client.get("family").body()

    /** POST /family/invitations — a code of 8 characters, valid 7 days. */
    suspend fun createInvitation(): InvitationDto = client.post("family/invitations").body()
}
