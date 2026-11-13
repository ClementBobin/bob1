package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.*
import com.mirage.bob1.domain.model.Season
import com.mirage.bob1.domain.model.Team

interface ClubRepository {
    suspend fun teams(): Result<List<Team>>
    suspend fun venue(id: String): Result<Venue>
    suspend fun seasons(): Result<List<Season>>
}
