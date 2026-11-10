package com.mirage.bob1ob1ob1ob1.data.dto

import kotlinx.serialization.Serializable
import com.mirage.bob1ob1ob1ob1.domain.model.*

@Serializable
data class DivisionDto(
    val id: String,
    val name: String,
    val category: String = "",
    val gender: String = "",
    val isActive: Boolean = true,
) {
    fun toDomain() = Division(id, name, category, gender, isActive)
}
