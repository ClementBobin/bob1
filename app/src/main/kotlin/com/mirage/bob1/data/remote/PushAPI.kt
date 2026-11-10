package com.mirage.bob1.data.remote

import com.mirage.bob1.data.dto.PushTokenDto
import com.mirage.bob1.di.AuthenticatedClient
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import javax.inject.Inject

internal class PushAPI @Inject constructor(@AuthenticatedClient private val client: HttpClient) {

    suspend fun registerToken(token: String) {
        client.post("me/push-tokens") { setBodyJson(PushTokenDto(token)) }.ok()
    }

    suspend fun unregisterToken(token: String) {
        client.delete("me/push-tokens") { setBodyJson(PushTokenDto(token)) }.ok()
    }
}
