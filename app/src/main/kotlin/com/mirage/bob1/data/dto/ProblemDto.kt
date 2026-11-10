package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDto(
    val title: String? = null,
    val detail: String? = null,
    val code: String? = null,
    val status: Int? = null,
) {
    /** What to show: the detail when the server gave one, else the title. */
    val text: String? get() = detail ?: title
}
