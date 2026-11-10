package com.mirage.bob1.domain.repository

import kotlinx.coroutines.flow.Flow
import com.mirage.bob1.domain.model.*

/** All suspend calls return [Result]; HTTP failures are `HttpException` (see data/remote). */
interface AuthRepository {
    /** `null` while the encrypted session is being read, then `true` / `false`. */
    val isAuthenticated: Flow<Boolean?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(form: Registration): Result<User>
    suspend fun me(): Result<User>
    suspend fun logout()
}
