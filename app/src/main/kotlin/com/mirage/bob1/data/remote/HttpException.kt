package com.mirage.bob1.data.remote

/**
 * Every failure of the HTTP client surfaces as one of these. The client has already told the user (toast + vibration),
 * so ViewModels do not toast them again (see `Bob1ViewModel.sendError`). [message] is a French sentence meant for the user.
 */
sealed class HttpException(message: String) : Exception(message) {
    class NotAccepted(message: String) : HttpException(message)

    /** 4xx. [code] is the stable `code` of the API's ProblemDetails (e.g. `ride.contention`), when there is one. */
    class ClientError(val statusCode: Int, message: String, val code: String? = null) : HttpException(message)

    class ServerError(val statusCode: Int, message: String) : HttpException(message)

    /** No answer from the server (offline, DNS, timeout...). */
    class NetworkError(message: String, cause: Throwable? = null) : HttpException(message) {
        init { cause?.let(::initCause) }
    }
}
