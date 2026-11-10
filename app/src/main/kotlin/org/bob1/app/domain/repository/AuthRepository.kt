package org.bob1.app.domain.repository

import kotlinx.coroutines.flow.Flow
import org.bob1.app.domain.model.*

/** All suspend calls return [Result]; failures are `ApiException` (see data/remote) for HTTP errors. */
interface AuthRepository {
    /** `null` while the encrypted session is being read, then `true` / `false`. */
    val isAuthenticated: Flow<Boolean?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(form: Registration): Result<User>
    suspend fun me(): Result<User>
    suspend fun logout()
}
