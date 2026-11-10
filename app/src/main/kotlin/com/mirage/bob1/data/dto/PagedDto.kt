package com.mirage.bob1.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedDto<T>(val total: Int = 0, val items: List<T> = emptyList())
