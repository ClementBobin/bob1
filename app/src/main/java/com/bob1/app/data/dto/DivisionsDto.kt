package com.bob1.app.data.dto

import com.bob1.app.domain.model.Division
import kotlinx.serialization.Serializable

@Serializable
data class DivisionDto(val id: String, val name: String) {
    fun toDomain() = Division(id = id, name = name)

    companion object {
        fun fromDomain(d: Division) = DivisionDto(id = d.id, name = d.name)
    }
}
