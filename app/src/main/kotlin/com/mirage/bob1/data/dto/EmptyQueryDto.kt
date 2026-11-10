package com.mirage.bob1ob1.data.dto

import kotlinx.serialization.Serializable

/** Body of every list endpoint that needs no criteria: serialises to `{}`. */
@Serializable class EmptyQueryDto
