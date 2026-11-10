package com.mirage.bob1.domain.model

data class Family(
    val id: String,
    val name: String,
    val members: List<FamilyMember>,
)
