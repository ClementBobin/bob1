package com.bob1.app.data.remote

import com.bob1.app.data.dto.AppSettingsDto
import com.bob1.app.data.dto.DivisionDto
import com.bob1.app.data.dto.PointRuleDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Passerelle vers les endpoints d'administration de l'API (/api/admin)
 */
internal class AdminAPI(private val client: HttpClient) {

    // ── Settings ──────────────────────────────────────────────────────────────

    suspend fun getSettings(): AppSettingsDto =
        client.get("/api/admin/settings").body()

    suspend fun updateSettings(settings: AppSettingsDto): AppSettingsDto =
        client.put("/api/admin/settings") {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }.body()

    // ── Point Rules ──────────────────────────────────────────────────────────

    suspend fun getPointRules(): List<PointRuleDto> =
        client.get("/api/admin/point-rules").body()

    suspend fun updatePointRule(rule: PointRuleDto): PointRuleDto =
        client.put("/api/admin/point-rules/${rule.id}") {
            contentType(ContentType.Application.Json)
            setBody(rule)
        }.body()

    // ── Divisions (Admin) ────────────────────────────────────────────────────

    suspend fun createDivision(name: String): DivisionDto =
        client.post("/api/divisions") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name))
        }.body()

    suspend fun deleteDivision(id: String): HttpResponse =
        client.delete("/api/divisions/$id")
}