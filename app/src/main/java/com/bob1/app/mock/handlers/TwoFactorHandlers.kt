package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.MessageResponse
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val twoFactorHandlers: List<MockHandler> = listOf(

    // POST /auth/2fa/setup → { secret, otpAuthUrl }
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/2fa/setup",
        resolver = { _, _ ->
            TwoFactorSetupDto(
                secret = "JBSWY3DPEHPK3PXP",
                otpAuthUrl = "otpauth://totp/bob1:admin@bob1.io?secret=JBSWY3DPEHPK3PXP&issuer=bob1"
            )
        }
    ),

    // POST /auth/2fa/confirm → { message } — code "000000" always fails
    MockHandler(
        method = HttpMethod.Post,
        path = "/auth/2fa/confirm",
        resolver = { _, body ->
            val json = body?.let { Json.parseToJsonElement(it).jsonObject }
            val code = json?.get("totpCode")?.jsonPrimitive?.content ?: ""
            if (code == "000000") error("Code TOTP invalide.")
            MessageResponse("Authentification à deux facteurs activée avec succès.")
        }
    ),
)