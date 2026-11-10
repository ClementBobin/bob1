package org.bob1.app.domain.model

import java.time.Instant

data class Invitation(val code: String, val expiresAt: Instant)
