package com.mirage.bob1.domain.model

import java.time.Instant

data class Invitation(val code: String, val expiresAt: Instant)
