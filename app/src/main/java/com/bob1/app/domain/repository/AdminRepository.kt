package com.bob1.app.domain.repository

import com.bob1.app.domain.model.AppSettings
import com.bob1.app.domain.model.Division
import com.bob1.app.domain.model.Match
import com.bob1.app.domain.model.PointRule

interface AdminRepository {
    suspend fun getSettings(): Result<AppSettings>
    suspend fun updateSettings(settings: AppSettings): Result<AppSettings>
    suspend fun createMatch(
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
    ): Result<Match>
    suspend fun updateMatch(matchId: String, params: Map<String, String>): Result<Match>
    suspend fun deleteMatch(matchId: String): Result<Unit>
    suspend fun getPointRules(): Result<List<PointRule>>
    suspend fun updatePointRule(rule: PointRule): Result<PointRule>
    suspend fun createDivision(name: String): Result<Division>
    suspend fun deleteDivision(id: String): Result<Unit>
}
