package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

/** No [name]: the API uses the person's last name. */
@Serializable
data class CreateFamilyRequestDto(val name: String? = null)
