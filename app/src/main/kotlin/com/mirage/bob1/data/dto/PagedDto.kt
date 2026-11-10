package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedDto<T>(val total: Int = 0, val items: List<T> = emptyList())
