package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.AuthResponseDto
import com.mirage.bob1.data.dto.BiometricDisableRequestDto
import com.mirage.bob1.data.dto.BiometricEnrollRequestDto
import com.mirage.bob1.data.dto.BiometricEnrollResponseDto
import com.mirage.bob1.data.dto.CreateFamilyRequestDto
import com.mirage.bob1.data.dto.FamilyDto
import com.mirage.bob1.data.dto.InvitationDto
import com.mirage.bob1.data.dto.JoinFamilyRequestDto
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

    /** POST /auth/biometric/enroll — the response holds the only copy of the device token. */
    suspend fun enrollBiometric(deviceName: String?): BiometricEnrollResponseDto =
        client.post("auth/biometric/enroll") { setBodyJson(BiometricEnrollRequestDto(deviceName)) }.body()

    /** POST /auth/biometric/disable — revokes one device's credential. */
    suspend fun disableBiometric(credentialId: String) {
        client.post("auth/biometric/disable") { setBodyJson(BiometricDisableRequestDto(credentialId)) }.ok()
    }

    suspend fun getFamily(): FamilyDto = client.get("family").body()

    /** POST /family — a parent starts a family; the answer is a fresh session (the family id travels in the access token). */
    suspend fun createFamily(name: String?): AuthResponseDto =
        client.post("family") { setBodyJson(CreateFamilyRequestDto(name)) }.body()

    /** POST /family/join — with a parent's code; fresh session in the answer. */
    suspend fun joinFamily(code: String): AuthResponseDto =
        client.post("family/join") { setBodyJson(JoinFamilyRequestDto(code)) }.body()

    /** POST /family/leave — the last member out deletes the family; fresh session in the answer. */
    suspend fun leaveFamily(): AuthResponseDto = client.post("family/leave").body()

    /** POST /family/invitations — a code of 8 characters, valid 7 days. */
    suspend fun createInvitation(): InvitationDto = client.post("family/invitations").body()
}
