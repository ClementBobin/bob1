package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.*

interface ClubRepository {
    suspend fun divisions(): Result<List<Division>>
    suspend fun venue(id: String): Result<Venue>
}
