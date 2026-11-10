package com.mirage.bob1.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

/** The API's list endpoints use the HTTP `QUERY` method with a JSON body. */
val QueryMethod = HttpMethod("QUERY")

inline fun <reified T> HttpRequestBuilder.setBodyJson(body: T) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

suspend inline fun <reified T> HttpClient.query(path: String, body: T): HttpResponse =
    request(path) {
        method = QueryMethod
        setBodyJson(body)
    }

fun HttpResponse.accept(vararg codes: HttpStatusCode) = apply {
    if (status !in codes) {
        throw HttpException.NotAccepted("Réponse inattendue : HTTP ${status.value} (attendu : ${codes.joinToString { it.value.toString() }})")
    }
}

/** For endpoints that answer 200, 201 or 204 and have nothing to read. */
fun HttpResponse.ok() = accept(HttpStatusCode.OK, HttpStatusCode.Created, HttpStatusCode.NoContent)
