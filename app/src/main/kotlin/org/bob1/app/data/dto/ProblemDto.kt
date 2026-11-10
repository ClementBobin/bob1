package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDto(
    val title: String? = null,
    val detail: String? = null,
    val code: String? = null,
    val status: Int? = null,
)
