package com.mirage.bob1.domain.repository

import com.mirage.bob1.domain.model.Family
import com.mirage.bob1.domain.model.Invitation
import com.mirage.bob1.domain.model.User

interface FamilyRepository {
    suspend fun family(): Result<Family>
    suspend fun createInvitation(): Result<Invitation>

    /** Starts a family (parents only). The session is renewed: the family id travels in the access token. */
    suspend fun create(name: String?): Result<User>

    /** Joins the family behind a parent's code. */
    suspend fun join(code: String): Result<User>

    /** Leaves the family; the last member out deletes it. */
    suspend fun leave(): Result<User>
}
