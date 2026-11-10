package com.mirage.bob1ob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequestDto(val refreshToken: String)
