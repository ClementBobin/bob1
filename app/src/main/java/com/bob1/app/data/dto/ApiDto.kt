package com.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val message: String? = null,
    val error: String? = null
) {
    val text: String get() = message ?: error ?: "No details provided"
}

@Serializable
data class MessageResponseDto(val message: String)