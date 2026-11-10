package org.bob1.app.domain.repository

import org.bob1.app.domain.model.*

interface FamilyRepository {
    suspend fun family(): Result<Family>
    suspend fun createInvitation(): Result<Invitation>
}
