package com.mirage.bob1.domain.model

data class Location(
    val id: String,
    val name: String,
    val address: String = "",
)