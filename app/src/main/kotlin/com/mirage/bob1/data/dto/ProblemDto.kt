package com.mirage.bob1ob1ob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDto(
    val title: String? = null,
    val detail: String? = null,
    val code: String? = null,
    val status: Int? = null,
)
