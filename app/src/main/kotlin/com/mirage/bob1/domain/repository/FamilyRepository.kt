package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.*

interface FamilyRepository {
    suspend fun family(): Result<Family>
    suspend fun createInvitation(): Result<Invitation>
}
