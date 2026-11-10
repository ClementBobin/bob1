package org.bob1.app.data.dto

import kotlinx.serialization.Serializable
import org.bob1.app.domain.model.*

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
