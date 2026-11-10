package org.bob1.app.domain.repository

import org.bob1.app.domain.model.*

interface ClubRepository {
    suspend fun divisions(): Result<List<Division>>
    suspend fun venue(id: String): Result<Venue>
}
