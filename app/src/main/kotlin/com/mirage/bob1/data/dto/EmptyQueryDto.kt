package org.bob1.app.data.dto

import kotlinx.serialization.Serializable

/** Body of every list endpoint that needs no criteria: serialises to `{}`. */
@Serializable class EmptyQueryDto
