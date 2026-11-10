package com.mirage.bob1.data.remote

import kotlin.coroutines.cancellation.CancellationException

/** Every repository call goes through here: failures become a [Result] failure (an [HttpException] for anything HTTP), cancellation is rethrown. */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.failure(e)
}
