package org.bob1.app.domain.model

data class Family(
    val id: String,
    val name: String,
    val members: List<FamilyMember>,
)
