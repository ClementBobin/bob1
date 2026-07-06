package com.bob1.app.domain.model

data class Location(
    val id: String,
    val name: String,
    val address: String = "",
)