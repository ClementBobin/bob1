package com.mirage.bob1.ui.core

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val fr = Locale.FRANCE
private val zone get() = ZoneId.systemDefault()

fun Instant.toLocalDate(): LocalDate = atZone(zone).toLocalDate()
fun Instant.formatTime(): String = DateTimeFormatter.ofPattern("HH:mm", fr).format(atZone(zone))
fun Instant.formatDayTime(): String = DateTimeFormatter.ofPattern("EEEE d MMMM · HH:mm", fr).format(atZone(zone)).capitalized()
fun Instant.formatShort(): String = DateTimeFormatter.ofPattern("d MMM · HH:mm", fr).format(atZone(zone))
fun LocalDate.formatLong(): String = DateTimeFormatter.ofPattern("EEEE d MMMM", fr).format(this).capitalized()
fun java.time.YearMonth.formatTitle(): String = DateTimeFormatter.ofPattern("MMMM yyyy", fr).format(this).capitalized()
private fun String.capitalized() = replaceFirstChar { it.titlecase(fr) }

/** What the user reads when something fails. HTTP errors already carry a French sentence (see `friendlyMessage`). */
fun Throwable.userMessage(): String = message?.takeIf { it.isNotBlank() } ?: "Une erreur est survenue."
