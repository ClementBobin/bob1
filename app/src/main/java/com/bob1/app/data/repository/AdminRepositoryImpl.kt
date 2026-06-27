package com.bob1.app.data.repository

import com.bob1.app.data.dto.AppSettingsDto
import com.bob1.app.data.dto.CreateMatchRequestDto
import com.bob1.app.data.dto.PointRuleDto
import com.bob1.app.data.remote.AdminAPI
import com.bob1.app.data.remote.MatchAPI
import com.bob1.app.domain.model.*
import com.bob1.app.domain.repository.AdminRepository

/**
 * Implémentation de [AdminRepository] utilisant [AdminAPI] et [MatchAPI]
 */
internal class AdminRepositoryImpl(
    private val adminAPI: AdminAPI,
    private val matchAPI: MatchAPI
) : AdminRepository {

    // ── Settings ──────────────────────────────────────────────────────────────

    override suspend fun getSettings(): Result<AppSettings> = runCatching {
        adminAPI.getSettings().toDomain()
    }

    override suspend fun updateSettings(settings: AppSettings): Result<AppSettings> = runCatching {
        adminAPI.updateSettings(AppSettingsDto.fromDomain(settings)).toDomain()
    }

    // ── Point Rules ──────────────────────────────────────────────────────────

    override suspend fun getPointRules(): Result<List<PointRule>> = runCatching {
        adminAPI.getPointRules().map { it.toDomain() }
    }

    override suspend fun updatePointRule(rule: PointRule): Result<PointRule> = runCatching {
        adminAPI.updatePointRule(PointRuleDto.fromDomain(rule)).toDomain()
    }

    // ── Match Management ─────────────────────────────────────────────────────

    override suspend fun createMatch(
        homeTeamId: String,
        awayTeamId: String,
        divisionId: String,
        dateIso: String,
        location: String,
        arbitreSlots: Int,
        chronoSlots: Int,
        marSlots: Int,
        emergencyDate: String?,
        emergencyPoints: Int,
    ): Result<Match> = runCatching {
        matchAPI.createMatch(
            CreateMatchRequestDto(
                homeTeamId = homeTeamId,
                awayTeamId = awayTeamId,
                divisionId = divisionId,
                dateIso = dateIso,
                location = location,
                arbitreSlots = arbitreSlots,
                chronoSlots = chronoSlots,
                marSlots = marSlots,
                emergencyDate = emergencyDate,
                emergencyPoints = emergencyPoints
            )
        ).toDomain()
    }

    override suspend fun updateMatch(
        matchId: String,
        params: Map<String, String>
    ): Result<Match> = runCatching {
        matchAPI.updateMatch(matchId, params).toDomain()
    }

    override suspend fun deleteMatch(matchId: String): Result<Unit> = runCatching {
        matchAPI.deleteMatch(matchId)
    }

    // ── Division Management ──────────────────────────────────────────────────

    override suspend fun createDivision(name: String): Result<Division> = runCatching {
        adminAPI.createDivision(name).toDomain()
    }

    override suspend fun deleteDivision(id: String): Result<Unit> = runCatching {
        adminAPI.deleteDivision(id)
    }
}