package com.bob1.app.domain.model

data class Team(
    val id: String,
    val name: String,
    val divisionId: String,
    val logoUrl: String? = null,
)
