package com.mirage.bob1ob1ob1ob1.data.dto

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * The API stores UTC in SQLite, so EF can hand back `DateTime` with Kind=Unspecified -> serialised WITHOUT a "Z".
 * Anything without an offset is therefore treated as UTC.
 */
fun parseUtc(value: String): Instant = runCatching { Instant.parse(value) }
    .recoverCatching { OffsetDateTime.parse(value).toInstant() }
    .getOrElse { LocalDateTime.parse(value).toInstant(ZoneOffset.UTC) }
