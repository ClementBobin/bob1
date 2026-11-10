package com.mirage.bob1.data.remote

/** HTTP failure with the stable `code` the API puts in its ProblemDetails (e.g. `ride.contention`). */
class ApiException(val status: Int, val code: String?, message: String, cause: Throwable? = null) : Exception(message, cause)
