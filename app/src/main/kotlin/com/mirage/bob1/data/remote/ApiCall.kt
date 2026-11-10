package org.bob1.app.data.remote

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.json.Json
import org.bob1.app.data.dto.ProblemDto
import retrofit2.HttpException

private val problemJson = Json { ignoreUnknownKeys = true; isLenient = true }

private fun HttpException.toApiException(): ApiException {
    val problem = runCatching { response()?.errorBody()?.string()?.let { problemJson.decodeFromString<ProblemDto>(it) } }.getOrNull()
    return ApiException(code(), problem?.code, problem?.detail ?: problem?.title ?: message(), this)
}

/** Every repository call goes through here: network + HTTP errors become a [Result] failure, cancellation is rethrown. */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    Result.failure(e.toApiException())
} catch (e: IOException) {
    Result.failure(ApiException(0, "network.unreachable", "Network unreachable", e))
} catch (e: Exception) {
    Result.failure(e)
}
