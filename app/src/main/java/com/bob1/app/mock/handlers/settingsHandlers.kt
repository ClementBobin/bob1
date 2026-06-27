package com.bob1.app.mock.handlers

import com.bob1.app.data.dto.AppSettingsDto
import com.bob1.app.mock.factories.BasketballMockData
import com.bob1.app.mock.registry.MockHandler
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private var mutableSettings = BasketballMockData.settings

val settingsHandlers: List<MockHandler> = listOf(

    MockHandler(HttpMethod.Get, "/admin/settings") { _, _ ->
        mutableSettings
    },

    MockHandler(HttpMethod.Put, "/admin/settings") { _, body ->
        val obj = body?.let { json.parseToJsonElement(it).jsonObject }
        val j15 = obj?.get("confirmationOffsetJ15")?.jsonPrimitive?.content?.toIntOrNull()
            ?: mutableSettings.confirmationOffsetJ15
        val j4  = obj?.get("confirmationOffsetJ4")?.jsonPrimitive?.content?.toIntOrNull()
            ?: mutableSettings.confirmationOffsetJ4
        mutableSettings = AppSettingsDto(j15, j4)
        mutableSettings
    },
)
